package com.inditex.pricing.infrastructure.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Price that applies to a product of a brand at the requested date")
public record PriceResponse(
        @Schema(example = "35455") Long productId,
        @Schema(example = "1") Long brandId,
        @Schema(description = "Price list (tariff) applied", example = "1") Integer priceList,
        @Schema(example = "2020-06-14T00:00:00") LocalDateTime startDate,
        @Schema(example = "2020-12-31T23:59:59") LocalDateTime endDate,
        @Schema(description = "Final sale price", example = "35.50") BigDecimal price,
        @Schema(description = "ISO 4217 currency code", example = "EUR") String currency) {
}
