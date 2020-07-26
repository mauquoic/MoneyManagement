package com.mauquoi.money.controller

import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.config.BusinessConfiguration
import com.mauquoi.money.model.Market
import com.mauquoi.money.util.TestObjectCreator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(MarketController::class)
@ActiveProfiles("test")
internal class MarketControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var stockService: StockService

    @MockkBean
    private lateinit var currencyService: CurrencyService

    @TestConfiguration
    class AdditionalConfig {
        @Bean
        fun markets(): List<Market> {
            return BusinessConfiguration().markets()
        }
    }

    private val capturedStockSymbol = slot<String>()
    private val capturedMarket = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }


    @Test
    fun getStockName() {
        every { stockService.getStockName(capture(capturedStockSymbol), capture(capturedMarket)) } returns TestObjectCreator.createExchange().stocks[0]

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/markets/US/stocks/ACN")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.`is`("Accenture")))
                .andExpect(MockMvcResultMatchers.jsonPath("market", CoreMatchers.`is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("symbol", CoreMatchers.`is`("ACN")))

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedStockSymbol.captured, Matchers.`is`("ACN")) },
                { MatcherAssert.assertThat(capturedMarket.captured, Matchers.`is`("US")) }
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

    @Test
    fun getMarkets() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/markets")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize<Int>(62)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].market", CoreMatchers.`is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", CoreMatchers.`is`("United States Exchanges")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].market", CoreMatchers.`is`("SW")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description", CoreMatchers.`is`("Swiss Exchange")))
    }
}