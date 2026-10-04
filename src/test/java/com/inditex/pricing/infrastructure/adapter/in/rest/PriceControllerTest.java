package com.inditex.pricing.infrastructure.adapter.in.rest;

import com.inditex.pricing.application.port.in.GetPriceUseCase;
import com.inditex.pricing.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
public class PriceControllerTest {

    private static final String URL = "/api/v1/prices";
    private static final LocalDateTime DATE = LocalDateTime.parse("2020-06-14T16:00:00");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetPriceUseCase getPriceUseCase;

    @Test
    @DisplayName("Given a price applies, when the endpoint is called, then it returns the price as JSON without priority")
    void returnsThePriceAsJson() throws Exception {
        // given
        when(getPriceUseCase.getPrice(1L, 35455L, DATE)).thenReturn(new Price(1L, 35455L, 2,
                LocalDateTime.parse("2020-06-14T15:00:00"), LocalDateTime.parse("2020-06-14T18:30:00"),
                1, new BigDecimal("25.45"), "EUR"));

        // when / then
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00"))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.priority").doesNotExist());
    }
}
