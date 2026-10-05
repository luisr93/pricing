package com.inditex.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class PriceApiIntegrationTest {

    private static final String URL = "/api/v1/prices";

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "{0}")
    @DisplayName("Given the example prices, when a date is requested, then the expected price list applies")
    @CsvSource(delimiter = '|', textBlock = """
            Test 1: day 14 at 10:00 | 2020-06-14T10:00:00 | 1 | 2020-06-14T00:00:00 | 2020-12-31T23:59:59 | 35.50
            Test 2: day 14 at 16:00 | 2020-06-14T16:00:00 | 2 | 2020-06-14T15:00:00 | 2020-06-14T18:30:00 | 25.45
            Test 3: day 14 at 21:00 | 2020-06-14T21:00:00 | 1 | 2020-06-14T00:00:00 | 2020-12-31T23:59:59 | 35.50
            Test 4: day 15 at 10:00 | 2020-06-15T10:00:00 | 3 | 2020-06-15T00:00:00 | 2020-06-15T11:00:00 | 30.50
            Test 5: day 16 at 21:00 | 2020-06-16T21:00:00 | 4 | 2020-06-15T16:00:00 | 2020-12-31T23:59:59 | 38.95
            """)
    void returnsThePrice(String scenario, String applicationDate, int priceList, String startDate, String endDate,
                         double price) throws Exception {
        // give the four example prices loaded by data.sql

        //when / then
        mockMvc.perform(get(URL)
                    .param("applicationDate", applicationDate)
                    .param("productId", "35455")
                    .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(priceList))
                .andExpect(jsonPath("$.startDate").value(startDate))
                .andExpect(jsonPath("$.endDate").value(endDate))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    @DisplayName("Given no price covers the date, when it is requested, then the API answers 404 with a problem detail")
    void returnsNotFoundOutsideEveryRange() throws Exception {
        // given no example price covers the application date

        // when / then
        mockMvc.perform(get(URL)
                    .param("applicationDate", "2021-01-01T00:00:00")
                    .param("productId", "35455")
                    .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"));
    }
}
