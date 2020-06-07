package com.mauquoi.money.business.service

import com.mauquoi.money.model.Account
import com.mauquoi.money.repository.AccountRepository
import com.mauquoi.money.repository.UserRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class AccountService @Inject constructor(private val userRepository: UserRepository,
                                         private val accountRepository: AccountRepository) {

    fun getAccounts(): List<Account> {
        return accountRepository.findAll()
    }

    fun addAccount(userId: Long, accountDto: Account): Account {
        val user = userRepository.findById(userId).get()
        val account = accountDto.copy(user = user)
        return accountRepository.save(account)
    }

    fun editAccount(id: Long, account: Account): Account {
        val savedAccount = accountRepository.findById(id)
        val editedAccount = savedAccount.get().copy(name = account.name,
                currency = account.currency,
                amount = account.amount,
                description = account.description)
        return accountRepository.save(editedAccount)
    }

    fun getAccount(id: Long): Account {
        return accountRepository.findById(id).get()
    }

    fun getTotalAccountValue(userId: Long): Int {
        val accounts = accountRepository.findAllBelongingToUser(userId)
        return accounts.sumBy { it.amount }
    }

}