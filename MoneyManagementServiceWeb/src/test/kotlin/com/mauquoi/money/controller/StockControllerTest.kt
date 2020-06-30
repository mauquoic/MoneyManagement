package com.mauquoi.money.controller

import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.business.service.StockService
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
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(StockController::class)
@ActiveProfiles("test")
internal class StockControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var stockService: StockService

    @MockkBean
    private lateinit var currencyService: CurrencyService

    private val capturedStockSymbol = slot<String>()
    private val capturedMarket = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun getStockValue() {
        every { stockService.getStockPrice(capture(capturedStockSymbol)) } returns 34.0

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stocks/ACN")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", CoreMatchers.`is`(34.0)))

        assertAll(
                { assertThat(capturedStockSymbol.captured, `is`("ACN")) }
        )
    }

    @Test
    fun getStockName() {
        every { stockService.getStockName(capture(capturedStockSymbol), capture(capturedMarket)) } returns TestObjectCreator.createExchangeDto().stocks[0]

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/markets/US/stocks/ACN")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("description", CoreMatchers.`is`("Accenture")))
                .andExpect(MockMvcResultMatchers.jsonPath("displaySymbol", CoreMatchers.`is`("ACN")))
                .andExpect(MockMvcResultMatchers.jsonPath("symbol", CoreMatchers.`is`("ACN")))

        assertAll(
                { assertThat(capturedStockSymbol.captured, `is`("ACN")) },
                { assertThat(capturedMarket.captured, `is`("US")) }
        )
    }

    @Test
    fun getMarket() {
        every { stockService.getStockExchange(capture(capturedMarket)) } returns listOf(TestObjectCreator.createUsStock(), TestObjectCreator.createChStock())
        every { currencyService.getCurrencyForMarket(any()) } returns Currency.getInstance("USD")

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/markets/US/stocks")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("market", CoreMatchers.`is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("currency", CoreMatchers.`is`("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[0].name", CoreMatchers.`is`("Accenture")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[1].name", CoreMatchers.`is`("Geberit")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[0].symbol", CoreMatchers.`is`("ACN")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[1].symbol", CoreMatchers.`is`("GEBN")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[0].lookup", CoreMatchers.`is`("ACN")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[1].lookup", CoreMatchers.`is`("GEBN.SW")))
    }
}