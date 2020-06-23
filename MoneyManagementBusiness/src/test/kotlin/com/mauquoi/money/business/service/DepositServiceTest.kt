package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.DepositNotFoundException
import com.mauquoi.money.business.util.TestObjectCreator
import com.mauquoi.money.model.Deposit
import com.mauquoi.money.model.User
import com.mauquoi.money.model.audit.DepositSnapshot
import com.mauquoi.money.repository.DepositRepository
import com.mauquoi.money.repository.UserRepository
import com.mauquoi.money.repository.snapshot.DepositSnapshotRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
internal class DepositServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var depositSnapshotRepository: DepositSnapshotRepository

    @MockK
    lateinit var depositRepository: DepositRepository

    private lateinit var depositService: DepositService
    private val capturedUserId = slot<Long>()
    private val capturedDepositId = slot<Long>()
    private val capturedAuditId = slot<Long>()
    private val capturedUser = slot<User>()
    private val capturedDeposit = slot<Deposit>()
    private val capturedDepositSnapshot = slot<DepositSnapshot>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        depositService = DepositService(userRepository = userRepository,
                depositSnapshotRepository = depositSnapshotRepository,
                depositRepository = depositRepository)
    }

    @Test
    fun getDeposits_resultsAreSorted() {
        every { depositRepository.findByUserId(capture(capturedUserId)) } returns TestObjectCreator.createDeposits().toSet()

        val deposits = depositService.getDeposits(1L)

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(deposits[0].id, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedUserId.captured, CoreMatchers.`is`(1L)) }
        )
    }

    @Test
    fun getAccount_depositDoesNotExist_errorThrown() {
        every { depositRepository.findById(any()) } returns Optional.empty()

        val err = assertThrows<DepositNotFoundException> { depositService.getDeposit(1L) }

        MatcherAssert.assertThat(err.localizedMessage, CoreMatchers.`is`("No deposit could be found by that ID."))
    }

    @Test
    fun addDeposit() {
        every { userRepository.findById(capture(capturedUserId)) } returns Optional.of(TestObjectCreator.createUser())
        every { depositRepository.save(capture(capturedDeposit)) } returns TestObjectCreator.createDeposit()

        val depositDto = TestObjectCreator.createDeposits()[0]
        depositDto.id = null
        depositService.addDeposit(1L, depositDto)

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedUserId.captured, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDeposit.captured.id, CoreMatchers.`is`(CoreMatchers.nullValue())) },
                { MatcherAssert.assertThat(capturedDeposit.captured.user!!.id, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDeposit.captured.amount, CoreMatchers.`is`(100F)) },
                { MatcherAssert.assertThat(capturedDeposit.captured.currency.currencyCode, CoreMatchers.`is`("CHF")) },
                { MatcherAssert.assertThat(capturedDeposit.captured.description, CoreMatchers.`is`("Description")) },
                { MatcherAssert.assertThat(capturedDeposit.captured.name, CoreMatchers.`is`("Deposit")) }
        )
    }

    @Test
    fun editDeposit() {
        every { depositRepository.findById(capture(capturedDepositId)) } returns Optional.of(TestObjectCreator.createDeposit())
        every { depositRepository.save(capture(capturedDeposit)) } returns TestObjectCreator.createDeposit()

        depositService.editDeposit(1L, TestObjectCreator.createDeposits()[1])

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedDepositId.captured, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDeposit.captured.id, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDeposit.captured.amount, CoreMatchers.`is`(200F)) },
                { MatcherAssert.assertThat(capturedDeposit.captured.currency.currencyCode, CoreMatchers.`is`("EUR")) },
                { MatcherAssert.assertThat(capturedDeposit.captured.description, CoreMatchers.`is`("2ndDescription")) },
                { MatcherAssert.assertThat(capturedDeposit.captured.name, CoreMatchers.`is`("2ndDeposit")) }
        )
    }

    @Test
    fun addDepositSnapshot_onlySnapshotIsAdded() {
        every { depositRepository.findById(capture(capturedDepositId)) } returns Optional.of(TestObjectCreator.createDeposit())
        every { depositSnapshotRepository.save(capture(capturedDepositSnapshot)) } returns TestObjectCreator.createDepositSnapshot()
        val snapshot = TestObjectCreator.createDepositSnapshotWithoutDeposit()
        depositService.addDepositSnapshot(1L, snapshot)

        verify(exactly = 0) { depositRepository.save<Deposit>(any()) }

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedDepositId.captured, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.id, CoreMatchers.`is`(CoreMatchers.nullValue())) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.deposit!!.id, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.amount, CoreMatchers.`is`(250f)) }
        )
    }

    @Test
    fun updateDepositValue_usingAmount_snapshotIsAdded_andDepositIsUpdated() {
        every { depositRepository.findById(capture(capturedDepositId)) } returns Optional.of(TestObjectCreator.createDeposit())
        every { depositSnapshotRepository.save(capture(capturedDepositSnapshot)) } returns TestObjectCreator.createDepositSnapshot()
        every { depositRepository.save(capture(capturedDeposit)) } returns TestObjectCreator.createDeposit()

        depositService.updateDepositValue(1L, amount = 550f)

        verify(exactly = 1) { depositRepository.save<Deposit>(any()) }

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedDepositId.captured, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.id, CoreMatchers.`is`(CoreMatchers.nullValue())) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.deposit!!.id, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.amount, CoreMatchers.`is`(550f)) },
                { MatcherAssert.assertThat(capturedDeposit.captured.amount, CoreMatchers.`is`(550f)) }
        )
    }

    @Test
    fun getDeposit() {
        every { depositRepository.findById(capture(capturedDepositId)) } returns Optional.of(TestObjectCreator.createDeposit())

        depositService.getDeposit(1L)

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedDepositId.captured, CoreMatchers.`is`(1L)) }
        )
    }

    @Test
    fun editAudit() {
        every { depositSnapshotRepository.findById(capture(capturedAuditId)) } returns Optional.of(TestObjectCreator.createDepositSnapshot())
        every { depositSnapshotRepository.save(capture(capturedDepositSnapshot)) } returns TestObjectCreator.createDepositSnapshot()

        val snapshot = TestObjectCreator.createDepositSnapshots()[1]
        depositService.editAudit(1L, snapshot)

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedAuditId.captured, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.id, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.deposit!!.id, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.amount, CoreMatchers.`is`(350f)) },
                { MatcherAssert.assertThat(capturedDepositSnapshot.captured.date, CoreMatchers.`is`(LocalDate.of(2020, 3, 1))) }
        )
    }

    @Test
    fun getHistory() {
        every { depositRepository.findById(capture(capturedDepositId)) } returns Optional.of(TestObjectCreator.createDeposit())
        every { depositSnapshotRepository.findByDepositId(capture(capturedAuditId)) } returns TestObjectCreator.createDepositSnapshots().toSet()

        val history = depositService.getHistory(1L)

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedDepositId.captured, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(capturedAuditId.captured, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(history.history.size, CoreMatchers.`is`(2)) },
                { MatcherAssert.assertThat(history.current.id, CoreMatchers.`is`(1L)) },
                { MatcherAssert.assertThat(history.current.amount, CoreMatchers.`is`(100f)) }
        )
    }
}