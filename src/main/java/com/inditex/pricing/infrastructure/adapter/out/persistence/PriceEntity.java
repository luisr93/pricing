package com.inditex.pricing.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRICES")
class PriceEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "BRAND_ID", nullable = false)
    private Long brandId;

    @Column(name = "START_DATE", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "PRICE_LIST", nullable = false)
    private Integer priceList;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "PRIORITY", nullable = false)
    private Integer priority;

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "CURRENCY", nullable = false, length = 3)
    private String currency;

    Long getBrandId() {
        return brandId;
    }

    LocalDateTime getStartDate() {
        return startDate;
    }

    LocalDateTime getEndDate() {
        return endDate;
    }

    Integer getPriceList() {
        return priceList;
    }

    Long getProductId() {
        return productId;
    }

    Integer getPriority() {
        return priority;
    }

    BigDecimal getPrice() {
        return price;
    }

    String getCurrency() {
        return currency;
    }
}
