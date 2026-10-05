package com.inditex.pricing.infrastructure.adapter.in.rest;

import com.inditex.pricing.application.port.in.GetPriceUseCase;
import com.inditex.pricing.infrastructure.adapter.in.rest.dto.PriceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/prices")
@Validated
@Tag(name = "Prices")
public class PriceController {

    private final GetPriceUseCase getPriceUseCase;

    public PriceController(GetPriceUseCase getPriceUseCase) {
        this.getPriceUseCase = getPriceUseCase;
    }

    @GetMapping
    @Operation(summary = "Get the price that applies to a product of a brand at a given date")
    @ApiResponse(responseCode = "200", description = "The applicable price")
    @ApiResponse(responseCode = "400", description = "Missing or invalid parameter",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No price applies at that date",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public PriceResponse getPrice(
            @Parameter(description = "Application date, ISO-8601", example = "2020-06-14T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @Parameter(description = "Product identifier", example = "35455")
            @RequestParam @Positive Long productId,
            @Parameter(description = "Brand identifier (1 = ZARA)", example = "1")
            @RequestParam @Positive Long brandId) {

        return PriceResponseMapper.toResponse(getPriceUseCase.getPrice(brandId, productId, applicationDate));

    }
}
