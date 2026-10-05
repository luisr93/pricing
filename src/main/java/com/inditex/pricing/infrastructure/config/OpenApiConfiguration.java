package com.inditex.pricing.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfiguration {

    @Bean
    OpenAPI pricingOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Pricing Service API")
                .description("Returns the price that applies to a product of a brand at a given date")
                .version("v1"));
    }
}
