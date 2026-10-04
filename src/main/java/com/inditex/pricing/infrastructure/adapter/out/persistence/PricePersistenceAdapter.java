package com.inditex.pricing.infrastructure.adapter.out.persistence;

import com.inditex.pricing.application.port.out.PriceRepositoryPort;
import com.inditex.pricing.domain.model.Price;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
class PricePersistenceAdapter implements PriceRepositoryPort {

    private final PriceJpaRepository priceJpaRepository;

    PricePersistenceAdapter(PriceJpaRepository priceJpaRepository) {
        this.priceJpaRepository = priceJpaRepository;
    }

    @Override
    public Optional<Price> findPrice(Long brandId, Long productId, LocalDateTime applicationDate) {
        return priceJpaRepository.findPrice(brandId, productId, applicationDate)
                .map(PriceEntityMapper::toDomain);
    }
}

