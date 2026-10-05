package com.inditex.pricing.infrastructure.adapter.out.persistence;

import com.inditex.pricing.domain.model.Price;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(PricePersistenceAdapter.class)
class PricePersistenceAdapterTest {

    private static final Long BRAND_ID = 1L;
    private static final Long PRODUCT_ID = 35455L;

    @Autowired
    private PricePersistenceAdapter pricePersistenceAdapter;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Given two overlapping prices, when the date falls in both, then the higher priority wins")
    void returnsTheHighestPriorityPriceWhenRangesOverlap() {
        // given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0, 0);

        // when
        Optional<Price> actual = pricePersistenceAdapter.findPrice(BRAND_ID, PRODUCT_ID, date);

        // then
        assertThat(actual).map(Price::priceList).contains(2);
    }

    @Test
    @DisplayName("Given a stored price, when it is loaded, then every column is mapped to the domain model")
    void mapsEveryColumnToTheDomainModel() {
        // given
        LocalDateTime date = LocalDateTime.of(2020, 06, 14, 10, 0, 0);

        // when
        Price price = pricePersistenceAdapter.findPrice(BRAND_ID, PRODUCT_ID, date).orElseThrow();

        // then
        assertThat(price.brandId()).isEqualTo(BRAND_ID);
        assertThat(price.productId()).isEqualTo(PRODUCT_ID);
        assertThat(price.priceList()).isEqualTo(1);
        assertThat(price.startDate()).isEqualTo(LocalDateTime.parse("2020-06-14T00:00:00"));
        assertThat(price.endDate()).isEqualTo(LocalDateTime.parse("2020-12-31T23:59:59"));
        assertThat(price.priority()).isZero();
        assertThat(price.price()).isEqualByComparingTo("35.50");
        assertThat(price.currency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Given a price valid from 15:00 to 18:30, when the date is exactly on a bound, then the price applies")
    void dateBoundsAreInclusive() {
        // when / then
        assertThat(pricePersistenceAdapter.findPrice(BRAND_ID, PRODUCT_ID, LocalDateTime.parse("2020-06-14T15:00:00")))
                .map(Price::priceList).contains(2);
        assertThat(pricePersistenceAdapter.findPrice(BRAND_ID, PRODUCT_ID, LocalDateTime.parse("2020-06-14T18:30:00")))
                .map(Price::priceList).contains(2);
        assertThat(pricePersistenceAdapter.findPrice(BRAND_ID, PRODUCT_ID, LocalDateTime.parse("2020-06-14T18:30:01")))
                .map(Price::priceList).contains(1);
    }

    @Test
    @DisplayName("Given two overlapping prices with the same priority, when both apply, then the higher price list wins")
    void breaksPriorityTiesByTheHighestPriceList() {
        // given
        insertPrice(5, 2);
        insertPrice(6, 2);

        // when
        Optional<Price> result = pricePersistenceAdapter.findPrice(BRAND_ID, 99L, LocalDateTime.parse("2020-06-01T12:00:00"));

        // then
        assertThat(result).map(Price::priceList).contains(6);
    }

    @Test
    @DisplayName("Given no matching price, when the date, product or brand matches nothing, then the result is empty")
    void returnsEmptyWhenNoPriceApplies() {
        // when / then
        assertThat(pricePersistenceAdapter.findPrice(BRAND_ID, PRODUCT_ID, LocalDateTime.parse("2021-01-01T00:00:00"))).isEmpty();
        assertThat(pricePersistenceAdapter.findPrice(BRAND_ID, 99999L, LocalDateTime.parse("2020-06-14T10:00:00"))).isEmpty();
        assertThat(pricePersistenceAdapter.findPrice(2L, PRODUCT_ID, LocalDateTime.parse("2020-06-14T10:00:00"))).isEmpty();
    }

    private void insertPrice(int priceList, int priority) {
        entityManager.createNativeQuery("""
                INSERT INTO PRICES (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURRENCY)
                VALUES (1, '2020-01-01 00:00:00', '2020-12-31 23:59:59', ?1, 99, ?2, 10.00, 'EUR')""")
                .setParameter(1, priceList)
                .setParameter(2, priority)
                .executeUpdate();
    }
}