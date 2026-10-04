package com.inditex.pricing.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    Optional<PriceEntity> findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
            Long brandId, Long productId, LocalDateTime startDate, LocalDateTime endDate);

    default Optional<PriceEntity> findPrice(Long brandId, Long productId, LocalDateTime applicationDate) {
        return findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
                brandId, productId, applicationDate, applicationDate);
    }
}
