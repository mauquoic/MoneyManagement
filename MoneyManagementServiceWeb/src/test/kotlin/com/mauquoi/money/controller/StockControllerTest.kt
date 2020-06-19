package com.mauquoi.money.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.business.service.UserService
import com.mauquoi.money.model.Stock
import com.mauquoi.money.model.UserPreferences
import com.mauquoi.money.util.TestObjectCreator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
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

    private val objectMapper : ObjectMapper = jacksonObjectMapper()

    private val capturedUserId = slot<Long>()
    private val capturedStockId = slot<Long>()
    private val capturedStock = slot<Stock>()
    private val capturedStockSymbol = slot<String>()
    private val capturedMarket = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @Test
    fun getStocks() {
        every { stockService.getStocks(capture(capturedUserId)) } returns TestObjectCreator.createStocks()

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/stocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.`is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].market", CoreMatchers.`is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].symbol", CoreMatchers.`is`("GEBN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", CoreMatchers.`is`("Geberit")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].currency", CoreMatchers.`is`("CHF")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].positions[0].purchasePrice", CoreMatchers.`is`(350.6)))

        assertThat(capturedUserId.captured, `is`(1L))
    }

    @Test
    fun getStock() {
        every { stockService.getStock(capture(capturedStockId)) } returns TestObjectCreator.createStocks()[1]

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/stocks/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("id", CoreMatchers.`is`(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("market", CoreMatchers.`is`("SW")))
                .andExpect(MockMvcResultMatchers.jsonPath("symbol", CoreMatchers.`is`("GEBN")))
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.`is`("Geberit")))
                .andExpect(MockMvcResultMatchers.jsonPath("currency", CoreMatchers.`is`("CHF")))
                .andExpect(MockMvcResultMatchers.jsonPath("positions[0].purchasePrice", CoreMatchers.`is`(350.6)))

        assertThat(capturedStockId.captured, `is`(2L))
    }

    @Test
    fun putStock() {
        every { stockService.editStock(capture(capturedStockId), capture(capturedStock)) } returns TestObjectCreator.createStocks()[1]

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1/stocks/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createStocks()[1])))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertAll(
                {assertThat(capturedStockId.captured, `is`(3L))},
                {assertThat(capturedStock.captured.symbol, `is`("GEBN"))},
                {assertThat(capturedStock.captured.name, `is`("Geberit"))}
        )
    }

    @Test
    fun addStock() {
        every { stockService.addStock(capture(capturedUserId), capture(capturedStock)) } returns TestObjectCreator.createStocks()[0]

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/2/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createStocks()[0])))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("id", CoreMatchers.`is`(1)))

        assertAll(
                {assertThat(capturedStock.captured.symbol, `is`("ACN"))},
                {assertThat(capturedStock.captured.name, `is`("Accenture"))}
        )
    }

    @Test
    fun getStockValue() {
        every { stockService.getStockPrice(capture(capturedStockSymbol)) } returns 34F

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stocks/ACN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", CoreMatchers.`is`(34.0)))

        assertAll(
                {assertThat(capturedStockSymbol.captured, `is`("ACN"))}
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
                {assertThat(capturedStockSymbol.captured, `is`("ACN"))},
                {assertThat(capturedMarket.captured, `is`("US"))}
        )
    }
}