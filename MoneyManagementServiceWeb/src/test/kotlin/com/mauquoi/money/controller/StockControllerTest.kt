package com.mauquoi.money.controller

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

@WebMvcTest(StockController::class)
@ActiveProfiles("test")
internal class StockControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var stockService: StockService

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
                .contentType(MediaType.APPLICATION_JSON))
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
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("description", CoreMatchers.`is`("Accenture")))
                .andExpect(MockMvcResultMatchers.jsonPath("displaySymbol", CoreMatchers.`is`("ACN")))
                .andExpect(MockMvcResultMatchers.jsonPath("symbol", CoreMatchers.`is`("ACN")))

        assertAll(
                { assertThat(capturedStockSymbol.captured, `is`("ACN")) },
                { assertThat(capturedMarket.captured, `is`("US")) }
        )
    }
}