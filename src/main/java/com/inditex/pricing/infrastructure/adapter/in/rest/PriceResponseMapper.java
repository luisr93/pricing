package com.inditex.pricing.infrastructure.adapter.in.rest;

import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.infrastructure.adapter.in.rest.dto.PriceResponse;

final class PriceResponseMapper {

    PriceResponseMapper() {}

    static PriceResponse toResponse(Price price) {
        return new PriceResponse(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.price(),
                price.currency());
    }
}
