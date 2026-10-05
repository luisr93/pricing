package com.inditex.pricing.application.service;

import com.inditex.pricing.application.port.out.PriceRepositoryPort;
import com.inditex.pricing.domain.exception.PriceNotFoundException;
import com.inditex.pricing.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    private static final Long BRAND_ID = 1L;
    private static final Long PRODUCT_ID = 25744L;
    private static final LocalDateTime DATE = LocalDateTime.of(2026, 10, 4, 10, 0);

    @Mock
    private PriceRepositoryPort priceRepository;

    @InjectMocks
    private PriceService sut;

    @Test
    @DisplayName("Given a price applies at the date, when the price is requested, then that price is returned")
    void returnsThePriceFoundByTheRepository() {
        // given
        Price expected = new Price(BRAND_ID, PRODUCT_ID, 1, LocalDateTime.of(2026, 10, 4, 0, 0),
                LocalDateTime.of(2026, 10, 4, 10, 0 ), 0,
                new BigDecimal("35.85"), "EUR");
        when(priceRepository.findPrice(BRAND_ID, PRODUCT_ID, DATE)).thenReturn(Optional.of(expected));

        // when
        Price actual = sut.getPrice(BRAND_ID, PRODUCT_ID, DATE);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given no price applies at the date, when the price is requested, then PriceNotFoundException is thrown")
    void throwsPriceNotFoundExceptionWhenNoPriceApplies() {
        // given
        when(priceRepository.findPrice(BRAND_ID, PRODUCT_ID, DATE)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> sut.getPrice(BRAND_ID, PRODUCT_ID, DATE))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessage("No applicable price found for brandId=1, productId=25744 at date=2026-10-04T10:00");
    }
}
