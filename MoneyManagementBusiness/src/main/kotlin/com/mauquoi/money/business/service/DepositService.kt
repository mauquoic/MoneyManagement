package com.mauquoi.money.business.service

import com.mauquoi.money.model.Deposit
import com.mauquoi.money.model.audit.DepositSnapshot
import com.mauquoi.money.model.history.DepositHistory
import com.mauquoi.money.repository.DepositRepository
import com.mauquoi.money.repository.UserRepository
import com.mauquoi.money.repository.snapshot.DepositSnapshotRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.inject.Inject

@Service
class DepositService @Inject constructor(private val userRepository: UserRepository,
                                         private val depositRepository: DepositRepository,
                                         private val depositSnapshotRepository: DepositSnapshotRepository) {

    fun getDeposits(userId: Long): List<Deposit> {
        return depositRepository.findByUserId(userId).toList().sortedBy { it.id }
    }

    fun getDeposit(id: Long): Deposit {
        return depositRepository.findById(id).get()
    }

    fun addDeposit(userId: Long, depositDto: Deposit): Deposit {
        val user = userRepository.findById(userId).get()
        val deposit = depositDto.copy(user = user)
        return depositRepository.save(deposit)
    }

    fun editDeposit(id: Long, deposit: Deposit) {
        val savedDeposit = depositRepository.findById(id)
        val editedDeposit = savedDeposit.get().copy(name = deposit.name,
                currency = deposit.currency,
                amount = deposit.amount,
                description = deposit.description)
        depositRepository.save(editedDeposit)
    }

    fun getTotalDepositsValue(userId: Long): Float {
        return getDeposits(userId).sumByDouble { it.amount.toDouble() }.toFloat()
    }

    fun addDepositSnapshot(depositId: Long, depositSnapshot: DepositSnapshot) {
        val deposit = getDeposit(depositId)
        val new = depositSnapshot.copy(deposit = deposit,
                date = LocalDate.now()
        )
        depositSnapshotRepository.save(new)
    }

    fun updateDepositValue(depositId: Long, amount: Float) {
        val deposit = getDeposit(depositId)
        val new = DepositSnapshot(deposit = deposit,
                amount = amount,
                date = LocalDate.now()
        )
        depositSnapshotRepository.save(new)
        val updatedDeposit = deposit.copy(amount = amount)
        depositRepository.save(updatedDeposit)
    }

    fun editAudit(auditId: Long, depositSnapshot: DepositSnapshot) {
        val entry = depositSnapshotRepository.findById(auditId).get()
        val new = entry.copy(date = depositSnapshot.date,
                amount = depositSnapshot.amount
        )
        depositSnapshotRepository.save(new)
    }

    fun getHistory(depositId: Long): DepositHistory {
        val deposit = getDeposit(depositId)
        val audits = depositSnapshotRepository.findByDepositId(depositId)
                .toList()
                .sortedBy { it.date }
        return DepositHistory(current = deposit, history = audits)
    }

}