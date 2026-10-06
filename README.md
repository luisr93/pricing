# pricing-service

Spring Boot REST service that returns the single price that applies to a product of a brand
at a given date.

When several price lists overlap for the same product and date, the one with the highest
`PRIORITY` wins.

## Stack

* Java 25
* Maven
* Spring Boot 4.1.1
* Spring Data JPA
* H2
* springdoc-openapi 3
* JUnit 5
* Mockito
* AssertJ
* ArchUnit

## Run

Requires JDK 25+ and Maven 3.9+.

```bash
mvn spring-boot:run
```

At startup H2 runs `schema.sql` and `data.sql`, which create the `PRICES` table and load the four
example rows from the brief.

| URL | Purpose |
|---|---|
| `GET http://localhost:8080/api/v1/prices` | Price query |
| `http://localhost:8080/swagger-ui.html` | Swagger UI  |
| `http://localhost:8080/v3/api-docs` | OpenAPI document |
| `http://localhost:8080/h2-console` | H2 console: JDBC URL `jdbc:h2:mem:pricingdb`, user `sa`, empty password |

## Test

```bash
mvn verify
```

22 tests, written in Given / When / Then style:

| Test                          | Type                                       | What it proves                                                                         |
|-------------------------------|--------------------------------------------|----------------------------------------------------------------------------------------|
| `PriceApiIntegrationTest`     | Integration (`@SpringBootTest`, real H2)   | The 5 required scenarios end to end, plus a 404                                        |
| `PriceControllerTest`         | Web slice (`@WebMvcTest`, mocked use case) | JSON contract, 404 and 400 problem responses, bad input never reaches the use case     |
| `PricePersistenceAdapterTest` | JPA slice (`@DataJpaTest`, real H2)        | Priority wins, inclusive bounds, tie-break, full column mapping, empty results         |
| `PriceServiceTest`            | Unit (Mockito)                             | The use case returns the price or throws `PriceNotFoundException`                      |
| `HexagonalArchitectureTest`   | ArchUnit                                   | Dependencies point inwards; domain is framework-free; application uses only `@Service` |

Required scenarios (product `35455`, brand `1`):

| Test | Date             | Price list | Price     |
|------|------------------|------------|-----------|
| 1    | 2020-06-14 10:00 | 1          | 35.50 EUR |
| 2    | 2020-06-14 16:00 | 2          | 25.45 EUR |
| 3    | 2020-06-14 21:00 | 1          | 35.50 EUR |
| 4    | 2020-06-15 10:00 | 3          | 30.50 EUR |
| 5    | 2020-06-16 21:00 | 4          | 38.95 EUR |

## API

`GET /api/v1/prices?applicationDate={date}&productId={id}&brandId={id}`

```bash
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

| Status | When                                         | Body                                    |
|--------|----------------------------------------------|-----------------------------------------|
| 200    | A price applies                              | `PriceResponse`, always a single object |
| 400    | Missing, malformed or non-positive parameter | RFC 7807 `application/problem+json`     |
| 404    | No price applies at that date                | RFC 7807 `application/problem+json`     |

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "No applicable price found for brandId=1, productId=35455 at 2021-01-01T00:00",
  "instance": "/api/v1/prices"
}
```

## Architecture

Hexagonal (ports and adapters). Every dependency points inwards: `infrastructure → application → domain`.

```
com.inditex.pricing
├── domain                         Price (record), PriceNotFoundException. Plain Java
├── application
│   ├── port/in                    GetPriceUseCase      (what the app offers)
│   ├── port/out                   PriceRepositoryPort  (what the app needs)
│   └── service                    PriceService (@Service) implements the use case
└── infrastructure                 Spring, JPA and web live here
    ├── adapter/in/rest            Controller, DTO, mapper, RFC 7807 exception handler
    ├── adapter/out/persistence    JPA entity, Spring Data repository, mapper, adapter
    └── config                     OpenAPI configuration
```

- The **domain** has no framework imports. The **application** layer's only Spring dependency is
  `@Service`, so component scanning picks up the service without a separate configuration class.
- The REST adapter only knows the inbound port. The persistence adapter only implements the outbound
  port. The two adapters never reference each other.
- JPA classes are package-private and never leave their adapter. The API returns its own DTO, never
  the domain model; for example, `priority` is internal and not exposed.
- `HexagonalArchitectureTest` (ArchUnit) enforces these rules on every build.

## Design decisions

- **One indexed query; the database picks the winner.** The repository filters by brand, product
  and date range, orders by `PRIORITY DESC, PRICE_LIST DESC` and returns `LIMIT 1`. No candidates
  are loaded into memory. The composite index
  `IDX_PRICES_LOOKUP (BRAND_ID, PRODUCT_ID, START_DATE, END_DATE, PRIORITY)` supports it.
- **Inclusive date bounds.** End dates like `23:59:59` mean "valid until and including". `BETWEEN`
  is inclusive, and a test checks that price list 2 still applies at exactly `18:30:00`.
- **Deterministic tie-break.** The brief doesn't cover two overlapping prices with the same
  priority. The highest price list wins, so the same request always gets the same answer.
- **Single result or 404.** Never an empty list or a `null` body.
- **Errors as RFC 7807 problem details.** The domain exception knows nothing about HTTP; the REST
  adapter maps it to 404, and invalid input to 400.
- **`@Service` as the only Spring dependency in the application layer.** It's a pragmatic choice:
  migrating away from Spring would mean removing one annotation.
- **Spring Boot 4 on Java 25.** The current major line and the current LTS.
- README document made with AI