package com.mauquoi.money.controller

import com.mauquoi.money.business.gateway.ecb.CurrencyConfiguration
import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.util.TestObjectCreator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(CurrencyController::class)
@ActiveProfiles("test")
internal class CurrencyControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var currencyService: CurrencyService

    private val capturedCurrency = slot<Currency>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun getCurrencies() {
        every { currencyService.getCurrencies() } returns CurrencyConfiguration().supportedCurrencies()

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/currencies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]", CoreMatchers.`is`("EUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]", CoreMatchers.`is`("CAD")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2]", CoreMatchers.`is`("HKD")))
    }

    @Test
    fun getConversionRates() {
        every { currencyService.getRates(capture(capturedCurrency)) } returns TestObjectCreator.createCurrencyLookup()

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/currencies/conversion-rates?base-currency=USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("base", CoreMatchers.`is`("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("rates.CHF", CoreMatchers.`is`(1.1)))

        assertThat(capturedCurrency.captured.currencyCode, `is`("USD"))
    }
}