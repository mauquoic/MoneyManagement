package com.mauquoi.money.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.model.*
import com.mauquoi.money.util.TestObjectCreator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate

@WebMvcTest(StockPositionController::class)
@ActiveProfiles("test")
internal class StockPositionControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var stockService: StockService
    @MockkBean
    private lateinit var currencyService: CurrencyService

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    private val capturedUserId = slot<Long>()
    private val capturedStockPositionId = slot<Long>()
    private val capturedStockPosition = slot<StockPosition>()
    private val capturedDividend = slot<Dividend>()
    private val capturedPosition = slot<Position>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @Test
    fun getStockPositions() {
        every { stockService.getStockPositions(capture(capturedUserId)) } returns TestObjectCreator.createStockPositions()

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/stock-positions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", `is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].stock.market", `is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].stock.symbol", `is`("GEBN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].stock.name", `is`("Geberit")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].stock.currency", `is`("CHF")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].positions[0].purchasePrice", `is`(350.6)))

        assertThat(capturedUserId.captured, `is`(1L))
    }

    @Test
    fun getStockPosition() {
        every { stockService.getStockPosition(capture(capturedStockPositionId)) } returns TestObjectCreator.createStockPositions()[1]

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/stock-positions/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("id", `is`(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("stock.market", `is`("SW")))
                .andExpect(MockMvcResultMatchers.jsonPath("stock.symbol", `is`("GEBN")))
                .andExpect(MockMvcResultMatchers.jsonPath("stock.name", `is`("Geberit")))
                .andExpect(MockMvcResultMatchers.jsonPath("stock.currency", `is`("CHF")))
                .andExpect(MockMvcResultMatchers.jsonPath("positions[0].purchasePrice", `is`(350.6)))

        assertThat(capturedStockPositionId.captured, `is`(2L))
    }

    @Test
    fun editStockPosition() {
        every { stockService.editStockPosition(capture(capturedStockPositionId), capture(capturedStockPosition)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1/stock-positions/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createStockPositions()[1])))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedStockPositionId.captured, `is`(3L)) },
                { assertThat(capturedStockPosition.captured.stock.symbol, `is`("GEBN")) },
                { assertThat(capturedStockPosition.captured.stock.name, `is`("Geberit")) }
        )
    }

    @Test
    fun addStockPosition() {
        every { stockService.addStockPosition(capture(capturedUserId), capture(capturedStockPosition)) } returns TestObjectCreator.createStockPositions()[0]
        every { currencyService.verifyCurrencyCompatibility(any(), any()) } just runs

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/2/stock-positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createStockPositions()[0])))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("id", `is`(1)))

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedStockPosition.captured.stock.symbol, `is`("ACN")) },
                { assertThat(capturedStockPosition.captured.stock.name, `is`("Accenture")) }
        )
    }
    @Test
    fun addStockPositionDividend() {
        every { stockService.addStockPositionDividend(capture(capturedStockPositionId), capture(capturedDividend)) } returns TestObjectCreator.createStockPositions()[0]

        val dividendDto = DividendDto(amount = 42.0, date = LocalDate.now())
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/2/stock-positions/4/dividends")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dividendDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("id", `is`(1)))

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedStockPositionId.captured, `is`(4L)) },
                { assertThat(capturedDividend.captured.id, `is`(nullValue())) },
                { assertThat(capturedDividend.captured.totalAmount, `is`(42.0)) }
        )
    }

    @Test
    fun addStockPositionPosition() {
        every { stockService.addStockPositionPosition(capture(capturedStockPositionId), capture(capturedPosition)) } returns TestObjectCreator.createStockPositions()[0]

        val positionDto = PositionDto(amount= 10, purchasePrice = 12.12, fees = 25.1, date = LocalDate.of(2020, 1, 9))

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/2/stock-positions/3/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(positionDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("id", `is`(1)))

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedStockPositionId.captured, `is`(3L)) },
                { assertThat(capturedPosition.captured.id, `is`(nullValue())) },
                { assertThat(capturedPosition.captured.amount, `is`(10)) },
                { assertThat(capturedPosition.captured.purchasePrice, `is`(12.12)) }
        )
    }
}