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

@WebMvcTest(PositionController::class)
@ActiveProfiles("test")
internal class PositionControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var stockService: StockService
    @MockkBean
    private lateinit var currencyService: CurrencyService

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    private val capturedUserId = slot<Long>()
    private val capturedPositionId = slot<Long>()
    private val capturedPosition = slot<Position>()
    private val capturedDividend = slot<Dividend>()
    private val capturedTransaction = slot<Transaction>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @Test
    fun getPositions() {
        every { stockService.getPositions(capture(capturedUserId)) } returns TestObjectCreator.createPositions()

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/positions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", `is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].stock.market", `is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].stock.symbol", `is`("GEBN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].stock.name", `is`("Geberit")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].stock.currency", `is`("CHF")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].transactions[0].purchasePrice", `is`(350.6)))

        assertThat(capturedUserId.captured, `is`(1L))
    }

    @Test
    fun getPosition() {
        every { stockService.getPosition(capture(capturedPositionId)) } returns TestObjectCreator.createPositions()[1]

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/positions/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("id", `is`(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("stock.market", `is`("SW")))
                .andExpect(MockMvcResultMatchers.jsonPath("stock.symbol", `is`("GEBN")))
                .andExpect(MockMvcResultMatchers.jsonPath("stock.name", `is`("Geberit")))
                .andExpect(MockMvcResultMatchers.jsonPath("stock.currency", `is`("CHF")))
                .andExpect(MockMvcResultMatchers.jsonPath("transactions[0].purchasePrice", `is`(350.6)))

        assertThat(capturedPositionId.captured, `is`(2L))
    }

    @Test
    fun editPosition() {
        every { stockService.editPosition(capture(capturedPositionId), capture(capturedPosition)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1/positions/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createPositions()[1])))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedPositionId.captured, `is`(3L)) },
                { assertThat(capturedPosition.captured.stock.symbol, `is`("GEBN")) },
                { assertThat(capturedPosition.captured.stock.name, `is`("Geberit")) }
        )
    }

    @Test
    fun addPosition() {
        every { stockService.addPosition(capture(capturedUserId), capture(capturedPosition)) } returns TestObjectCreator.createPositions()[0]
        every { currencyService.verifyCurrencyCompatibility(any(), any()) } just runs

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/2/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createPositions()[0])))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("id", `is`(1)))

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedPosition.captured.stock.symbol, `is`("ACN")) },
                { assertThat(capturedPosition.captured.stock.name, `is`("Accenture")) }
        )
    }
    @Test
    fun addDividend() {
        every { stockService.addDividend(capture(capturedPositionId), capture(capturedDividend)) } returns TestObjectCreator.createPositions()[0]

        val dividendDto = DividendDto(amount = 42.0, date = LocalDate.now())
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/2/positions/4/dividends")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dividendDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("id", `is`(1)))

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedPositionId.captured, `is`(4L)) },
                { assertThat(capturedDividend.captured.id, `is`(nullValue())) },
                { assertThat(capturedDividend.captured.totalAmount, `is`(42.0)) }
        )
    }

    @Test
    fun addTransaction() {
        every { stockService.addTransaction(capture(capturedPositionId), capture(capturedTransaction)) } returns TestObjectCreator.createPositions()[0]

        val positionDto = TransactionDto(amount= 10, purchasePrice = 12.12, fees = 25.1, date = LocalDate.of(2020, 1, 9))

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/2/positions/3/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(positionDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("id", `is`(1)))

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedPositionId.captured, `is`(3L)) },
                { assertThat(capturedTransaction.captured.id, `is`(nullValue())) },
                { assertThat(capturedTransaction.captured.amount, `is`(10)) },
                { assertThat(capturedTransaction.captured.purchasePrice, `is`(12.12)) }
        )
    }
}