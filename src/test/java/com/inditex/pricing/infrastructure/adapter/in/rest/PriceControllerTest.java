package com.inditex.pricing.infrastructure.adapter.in.rest;

import com.inditex.pricing.application.port.in.GetPriceUseCase;
import com.inditex.pricing.domain.exception.PriceNotFoundException;
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

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

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

    @Test
    @DisplayName("Given no price applies, when the endpoint is called, then it returns a 404 problem detail")
    void returns404ProblemWhenNoPriceApplies() throws Exception {
        // given
        when(getPriceUseCase.getPrice(1L, 35455L, DATE))
                .thenThrow(new PriceNotFoundException(1L, 35455L, DATE));

        // when / then
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No applicable price found for brandId=1, productId=35455 at date=2020-06-14T16:00"));
    }

    @Test
    @DisplayName("Given productId is missing, when the endpoint is called, then it returns a 400 problem detail")
    void returns400WhenAParameterIsMissing() throws Exception {
        // when / then
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Missing required parameter: productId"));
        verifyNoInteractions(getPriceUseCase);
    }

    @Test
    @DisplayName("Given a malformed date, when the endpoint is called, then it returns a 400 problem detail")
    void returns400WhenTheDateIsMalformed() throws Exception {
        // when / then
        mockMvc.perform(get(URL)
                        .param("applicationDate", "14/06/2020 16:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Invalid value for parameter: applicationDate"));
        verifyNoInteractions(getPriceUseCase);
    }

    @Test
    @DisplayName("Given a non-positive productId, when the endpoint is called, then it returns a 400 problem detail")
    void returns400WhenAnIdIsNotPositive() throws Exception {
        // when / then
        mockMvc.perform(get(URL)
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", "-1")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
        verifyNoInteractions(getPriceUseCase);
    }
}
