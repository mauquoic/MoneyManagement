package com.mauquoi.money.business.service

import com.mauquoi.money.model.Account
import com.mauquoi.money.repository.AccountRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class AccountService @Inject constructor(private val accountRepository: AccountRepository) {

    fun getAccounts(): List<Account> {
        return accountRepository.findAll()
    }

    fun addAccount(account: Account): Account {
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

}