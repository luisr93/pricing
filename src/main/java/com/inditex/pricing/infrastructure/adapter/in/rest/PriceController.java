package com.inditex.pricing.infrastructure.adapter.in.rest;

import com.inditex.pricing.application.port.in.GetPriceUseCase;
import com.inditex.pricing.infrastructure.adapter.in.rest.dto.PriceResponse;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/prices")
@Validated
public class PriceController {

    private final GetPriceUseCase getPriceUseCase;

    public PriceController(GetPriceUseCase getPriceUseCase) {
        this.getPriceUseCase = getPriceUseCase;
    }

    @GetMapping
    public PriceResponse getPrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam @Positive Long productId,
            @RequestParam @Positive Long brandId) {

        return PriceResponseMapper.toResponse(getPriceUseCase.getPrice(brandId, productId, applicationDate));

    }
}
