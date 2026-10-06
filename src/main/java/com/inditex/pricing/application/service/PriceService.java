package com.inditex.pricing.application.service;

import com.inditex.pricing.application.port.in.GetPriceUseCase;
import com.inditex.pricing.application.port.out.PriceRepositoryPort;
import com.inditex.pricing.domain.exception.PriceNotFoundException;
import com.inditex.pricing.domain.model.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PriceService implements GetPriceUseCase {

    private static final Logger log = LoggerFactory.getLogger(PriceService.class);

    private final PriceRepositoryPort priceRepositoryPort;

    public PriceService(PriceRepositoryPort priceRepositoryPort) {
        this.priceRepositoryPort = priceRepositoryPort;
    }

    @Override
    public Price getPrice(Long brandId, Long productId, LocalDateTime applicationDate) {
        log.debug("Looking up price for brandId={}, productId={}, applicationDate={}", brandId, productId, applicationDate);
        return priceRepositoryPort.findPrice(brandId, productId, applicationDate)
                .orElseThrow(() -> new PriceNotFoundException(brandId, productId, applicationDate));
    }
}
