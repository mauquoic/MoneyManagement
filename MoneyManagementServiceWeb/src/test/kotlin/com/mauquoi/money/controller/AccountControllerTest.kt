package com.mauquoi.money.controller

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mauquoi.money.business.service.AccountService
import com.mauquoi.money.model.Account
import com.mauquoi.money.model.audit.AccountSnapshot
import com.mauquoi.money.model.history.AccountHistory
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

@WebMvcTest(AccountController::class)
@ActiveProfiles("test")
internal class AccountControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var accountService: AccountService

    private val objectMapper = jacksonObjectMapper()

    private val capturedUserId = slot<Long>()
    private val capturedAccountId = slot<Long>()
    private val capturedAccount = slot<Account>()
    private val capturedSnapshotId = slot<Long>()
    private val capturedSnapshot = slot<AccountSnapshot>()
    private val capturedAmount = slot<Float>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    @Test
    fun getAccounts() {
        every { accountService.getAccounts(capture(capturedUserId)) } returns TestObjectCreator.createAccounts()

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", CoreMatchers.`is`("Account")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", CoreMatchers.`is`(2)))

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) }
        )
    }

    @Test
    fun addAccount() {
        every { accountService.addAccount(capture(capturedUserId), capture(capturedAccount)) } returns TestObjectCreator.createAccount()
        val account = TestObjectCreator.createAccount()
        account.id = null

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.`is`("Account")))
                .andExpect(MockMvcResultMatchers.jsonPath("id", CoreMatchers.`is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("currency", CoreMatchers.`is`("CHF")))
                .andExpect(MockMvcResultMatchers.jsonPath("description", CoreMatchers.`is`("Description")))
                .andExpect(MockMvcResultMatchers.jsonPath("amount", CoreMatchers.`is`(100.0)))

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(capturedAccount.captured.id, `is`(nullValue())) },
                { assertThat(capturedAccount.captured.amount, `is`(100f)) }
        )
    }

    @Test
    fun getAccount() {
        every { accountService.getAccount(capture(capturedAccountId)) } returns TestObjectCreator.createAccount()

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/accounts/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.`is`("Account")))
                .andExpect(MockMvcResultMatchers.jsonPath("id", CoreMatchers.`is`(1)))

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(2L)) }
        )
    }

    @Test
    fun editAccount() {
        every { accountService.editAccount(capture(capturedAccountId), capture(capturedAccount)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1/accounts/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createAccount())))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(3L)) },
                { assertThat(capturedAccount.captured.amount, `is`(100f)) }
        )
    }

    @Test
    fun updateAccount() {
        every { accountService.updateAccountValue(capture(capturedAccountId), capture(capturedAmount)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/1/accounts/4/update?amount=2500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(4L)) },
                { assertThat(capturedAmount.captured, `is`(2500f)) }
        )
    }

    @Test
    fun addAccountAudit() {
        every { accountService.addAccountSnapshot(capture(capturedAccountId), capture(capturedSnapshot)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/1/accounts/5/audits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createAccountSnapshotWithoutAccount())))
                .andExpect(MockMvcResultMatchers.status().isCreated)

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(5L)) }
        )
    }

    @Test
    fun editAccountAudit() {
        every { accountService.editAudit(capture(capturedSnapshotId), capture(capturedSnapshot)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1/accounts/3/audits/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestObjectCreator.createAccountSnapshot())))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertAll(
                { assertThat(capturedSnapshotId.captured, `is`(2L)) },
                { assertThat(capturedSnapshot.captured.amount, `is`(250f)) },
                { assertThat(capturedSnapshot.captured.date, `is`(LocalDate.of(2020, 1, 1))) }
        )
    }

    @Test
    fun getAccountHistory() {
        val history = AccountHistory(
                current = TestObjectCreator.createAccount(),
                history = TestObjectCreator.createAccountSnapshots()
        )
        every { accountService.getHistory(capture(capturedAccountId)) } returns history

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/accounts/4/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("current.name", CoreMatchers.`is`("Account")))
                .andExpect(MockMvcResultMatchers.jsonPath("history[0].id", CoreMatchers.`is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("history[0].amount", CoreMatchers.`is`(250.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("history[1].id", CoreMatchers.`is`(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("history[1].amount", CoreMatchers.`is`(350.0)))

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(4L)) }
        )
    }
}