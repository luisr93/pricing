package com.inditex.pricing.application.port.out;

import com.inditex.pricing.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepositoryPort {

    Optional<Price> findPrice(Long brandId, Long productId, LocalDateTime applicationDate);
}
