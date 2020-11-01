package com.mauquoi.money.controller

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mauquoi.money.business.service.DepositService
import com.mauquoi.money.model.Deposit
import com.mauquoi.money.model.audit.DepositSnapshot
import com.mauquoi.money.model.history.DepositHistory
import com.mauquoi.money.util.TestObjectCreator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
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
import java.time.LocalDate

@WebMvcTest(DepositController::class)
@ActiveProfiles("test")
internal class DepositControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var depositService: DepositService

    private val objectMapper = jacksonObjectMapper()

    private val capturedUserId = slot<Long>()
    private val capturedDepositId = slot<Long>()
    private val capturedDeposit = slot<Deposit>()
    private val capturedSnapshotId = slot<Long>()
    private val capturedSnapshot = slot<DepositSnapshot>()
    private val capturedAmount = slot<Double>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @Test
    fun getDeposits() {
        every { depositService.getDeposits(capture(capturedUserId)) } returns TestObjectCreator.createDeposits()

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/deposits")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", CoreMatchers.`is`("Deposit")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", CoreMatchers.`is`(2)))

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) }
        )
    }

    @Test
    fun addDeposit() {
        every { depositService.addDeposit(capture(capturedUserId), capture(capturedDeposit)) } returns TestObjectCreator.createDeposit()
        val deposit = TestObjectCreator.createDeposit()
        deposit.id = null

        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deposit)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.`is`("Deposit")))
                .andExpect(MockMvcResultMatchers.jsonPath("id", CoreMatchers.`is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("currency", CoreMatchers.`is`("CHF")))
                .andExpect(MockMvcResultMatchers.jsonPath("description", CoreMatchers.`is`("Description")))
                .andExpect(MockMvcResultMatchers.jsonPath("amount", CoreMatchers.`is`(100.0)))

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(capturedDeposit.captured.id, `is`(nullValue())) },
                { assertThat(capturedDeposit.captured.amount, `is`(100.0)) }
        )
    }

    @Test
    fun getDeposit() {
        every { depositService.getDeposit(capture(capturedDepositId)) } returns TestObjectCreator.createDeposit()

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/deposits/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.`is`("Deposit")))
                .andExpect(MockMvcResultMatchers.jsonPath("id", CoreMatchers.`is`(1)))

        assertAll(
                { assertThat(capturedDepositId.captured, `is`(2L)) }
        )
    }

    @Test
    fun editDeposit() {
        every { depositService.editDeposit(capture(capturedDepositId), capture(capturedDeposit)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/deposits/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createDeposit())))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertAll(
                { assertThat(capturedDepositId.captured, `is`(3L)) },
                { assertThat(capturedDeposit.captured.amount, `is`(100.0)) }
        )
    }

    @Test
    fun updateDeposit() {
        every { depositService.updateDepositValue(capture(capturedDepositId), capture(capturedAmount)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/deposits/4/update?amount=2500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertAll(
                { assertThat(capturedDepositId.captured, `is`(4L)) },
                { assertThat(capturedAmount.captured, `is`(2500.0)) }
        )
    }

    @Test
    fun addDepositAudit() {
        every { depositService.addDepositSnapshot(capture(capturedDepositId), capture(capturedSnapshot)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/deposits/5/audits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createDepositSnapshotWithoutDeposit())))
                .andExpect(MockMvcResultMatchers.status().isCreated)

        assertAll(
                { assertThat(capturedDepositId.captured, `is`(5L)) }
        )
    }

    @Test
    fun editDepositAudit() {
        every { depositService.editAudit(capture(capturedSnapshotId), capture(capturedSnapshot)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/deposits/3/audits/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createDepositSnapshot())))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertAll(
                { assertThat(capturedSnapshotId.captured, `is`(2L)) },
                { assertThat(capturedSnapshot.captured.amount, `is`(250.0)) },
                { assertThat(capturedSnapshot.captured.date, `is`(LocalDate.of(2020, 1, 1))) }
        )
    }

    @Test
    fun getDepositHistory() {
        val history = DepositHistory(
                current = TestObjectCreator.createDeposit(),
                history = TestObjectCreator.createDepositSnapshots()
        )
        every { depositService.getHistory(capture(capturedDepositId)) } returns history

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/deposits/4/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("current.name", CoreMatchers.`is`("Deposit")))
                .andExpect(MockMvcResultMatchers.jsonPath("history[0].id", CoreMatchers.`is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("history[0].amount", CoreMatchers.`is`(250.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("history[1].id", CoreMatchers.`is`(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("history[1].amount", CoreMatchers.`is`(350.0)))

        assertAll(
                { assertThat(capturedDepositId.captured, `is`(4L)) }
        )
    }
}