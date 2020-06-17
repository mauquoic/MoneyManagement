package com.mauquoi.money.business.service

import com.mauquoi.money.business.util.TestObjectCreator
import com.mauquoi.money.model.Account
import com.mauquoi.money.model.User
import com.mauquoi.money.model.audit.AccountSnapshot
import com.mauquoi.money.model.history.AccountHistory
import com.mauquoi.money.repository.AccountRepository
import com.mauquoi.money.repository.UserRepository
import com.mauquoi.money.repository.audit.AccountAuditRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
internal class AccountServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var accountAuditRepository: AccountAuditRepository

    @MockK
    lateinit var accountRepository: AccountRepository

    private lateinit var accountService: AccountService
    private val capturedUserId = slot<Long>()
    private val capturedAccountId = slot<Long>()
    private val capturedAuditId = slot<Long>()
    private val capturedUser = slot<User>()
    private val capturedAccount = slot<Account>()
    private val capturedAccountSnapshot = slot<AccountSnapshot>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        accountService = AccountService(userRepository = userRepository,
                accountAuditRepository = accountAuditRepository,
                accountRepository = accountRepository)
    }

    @Test
    fun getAccounts_resultsAreSorted() {
        every { accountRepository.findByUserId(capture(capturedUserId)) } returns TestObjectCreator.createAccounts().toSet()

        val accounts = accountService.getAccounts(1L)

        assertAll(
                { assertThat(accounts[0].id, `is`(1L)) },
                { assertThat(capturedUserId.captured, `is`(1L)) }
        )
    }

    @Test
    fun addAccount() {
        every { userRepository.findById(capture(capturedUserId)) } returns Optional.of(TestObjectCreator.createUser())
        every { accountRepository.save(capture(capturedAccount)) } returns TestObjectCreator.createAccount()

        val accountDto = TestObjectCreator.createAccounts()[0]
        accountDto.id = null
        accountService.addAccount(1L, accountDto)

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(capturedAccount.captured.id, `is`(nullValue())) },
                { assertThat(capturedAccount.captured.user!!.id, `is`(1L)) },
                { assertThat(capturedAccount.captured.amount, `is`(100F)) },
                { assertThat(capturedAccount.captured.currency.currencyCode, `is`("CHF")) },
                { assertThat(capturedAccount.captured.description, `is`("Description")) },
                { assertThat(capturedAccount.captured.name, `is`("Account")) }
        )
    }

    @Test
    fun editAccount() {
        every { accountRepository.findById(capture(capturedAccountId)) } returns Optional.of(TestObjectCreator.createAccount())
        every { accountRepository.save(capture(capturedAccount)) } returns TestObjectCreator.createAccount()

        accountService.editAccount(1L, TestObjectCreator.createAccounts()[1])

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(1L)) },
                { assertThat(capturedAccount.captured.id, `is`(1L)) },
                { assertThat(capturedAccount.captured.amount, `is`(200F)) },
                { assertThat(capturedAccount.captured.currency.currencyCode, `is`("EUR")) },
                { assertThat(capturedAccount.captured.description, `is`("2ndDescription")) },
                { assertThat(capturedAccount.captured.name, `is`("2ndAccount")) }
        )
    }

    @Test
    fun addAccountSnapshot_onlySnapshotIsAdded() {
        every { accountRepository.findById(capture(capturedAccountId)) } returns Optional.of(TestObjectCreator.createAccount())
        every { accountAuditRepository.save(capture(capturedAccountSnapshot)) } returns TestObjectCreator.createAccountSnapshot()
        val snapshot = TestObjectCreator.createAccountSnapshotWithoutAccount()
        accountService.addAccountSnapshot(1L, snapshot)

        verify(exactly = 0) { accountRepository.save<Account>(any()) }

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(1L)) },
                { assertThat(capturedAccountSnapshot.captured.id, `is`(nullValue())) },
                { assertThat(capturedAccountSnapshot.captured.account!!.id, `is`(1L)) },
                { assertThat(capturedAccountSnapshot.captured.amount, `is`(250f)) }
        )
    }

    @Test
    fun updateAccountValue_usingAmount_snapshotIsAdded_andAccountIsUpdated() {
        every { accountRepository.findById(capture(capturedAccountId)) } returns Optional.of(TestObjectCreator.createAccount())
        every { accountAuditRepository.save(capture(capturedAccountSnapshot)) } returns TestObjectCreator.createAccountSnapshot()
        every { accountRepository.save(capture(capturedAccount)) } returns TestObjectCreator.createAccount()

        accountService.updateAccountValue(1L, amount = 550f)

        verify(exactly = 1) { accountRepository.save<Account>(any()) }

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(1L)) },
                { assertThat(capturedAccountSnapshot.captured.id, `is`(nullValue())) },
                { assertThat(capturedAccountSnapshot.captured.account!!.id, `is`(1L)) },
                { assertThat(capturedAccountSnapshot.captured.amount, `is`(550f)) },
                { assertThat(capturedAccount.captured.amount, `is`(550f)) }
        )
    }

    @Test
    fun getAccount() {
        every { accountRepository.findById(capture(capturedAccountId)) } returns Optional.of(TestObjectCreator.createAccount())

        accountService.getAccount(1L)

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(1L)) }
        )
    }

    @Test
    fun editAudit() {
        every { accountAuditRepository.findById(capture(capturedAuditId)) } returns Optional.of(TestObjectCreator.createAccountSnapshot())
        every { accountAuditRepository.save(capture(capturedAccountSnapshot)) } returns TestObjectCreator.createAccountSnapshot()

        val snapshot = TestObjectCreator.createAccountSnapshots()[1]
        accountService.editAudit(1L, snapshot)

        assertAll(
                { assertThat(capturedAuditId.captured, `is`(1L)) },
                { assertThat(capturedAccountSnapshot.captured.id, `is`(1L)) },
                { assertThat(capturedAccountSnapshot.captured.account!!.id, `is`(1L)) },
                { assertThat(capturedAccountSnapshot.captured.amount, `is`(350f)) },
                { assertThat(capturedAccountSnapshot.captured.date, `is`(LocalDate.of(2020, 3, 1))) }
        )
    }

    @Test
    fun getHistory() {
        every { accountRepository.findById(capture(capturedAccountId)) } returns Optional.of(TestObjectCreator.createAccount())
        every { accountAuditRepository.findByAccountId(capture(capturedAuditId)) } returns TestObjectCreator.createAccountSnapshots().toSet()

        val history = accountService.getHistory(1L)

        assertAll(
                { assertThat(capturedAccountId.captured, `is`(1L)) },
                { assertThat(capturedAuditId.captured, `is`(1L)) },
                { assertThat(history.history.size, `is`(2)) },
                { assertThat(history.current.id, `is`(1L)) },
                { assertThat(history.current.amount, `is`(100f)) }
        )
    }

}