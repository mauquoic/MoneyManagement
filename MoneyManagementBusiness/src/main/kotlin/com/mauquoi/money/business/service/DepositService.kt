package com.mauquoi.money.business.service

import com.mauquoi.money.model.Deposit
import com.mauquoi.money.repository.DepositRepository
import com.mauquoi.money.repository.UserRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class DepositService @Inject constructor(private val userRepository: UserRepository,
                                         private val depositRepository: DepositRepository) {

    fun getDeposits(userId: Long): List<Deposit> {
        return depositRepository.findAllBelongingToUser(userId).toList().sortedBy { it.id }
    }

    fun getDeposit(id: Long): Deposit {
        return depositRepository.findById(id).get()
    }

    fun addDeposit(userId: Long, depositDto: Deposit): Deposit {
        val user = userRepository.findById(userId).get()
        val deposit = depositDto.copy(user = user)
        return depositRepository.save(deposit)
    }

    fun editDeposit(id: Long, deposit: Deposit): Deposit {
        val savedDeposit = depositRepository.findById(id)
        val editedDeposit = savedDeposit.get().copy(name = deposit.name,
                currency = deposit.currency,
                amount = deposit.amount,
                description = deposit.description)
        return depositRepository.save(editedDeposit)
    }

    fun getTotalDepositsValue(userId: Long): Int {
        return getDeposits(userId).sumBy { it.amount }
    }

}