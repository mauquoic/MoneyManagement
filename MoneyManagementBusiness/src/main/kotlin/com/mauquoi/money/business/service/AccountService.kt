package com.mauquoi.money.business.service

import com.mauquoi.money.model.Account
import com.mauquoi.money.model.audit.AccountSnapshot
import com.mauquoi.money.model.history.AccountHistory
import com.mauquoi.money.repository.AccountRepository
import com.mauquoi.money.repository.UserRepository
import com.mauquoi.money.repository.snapshot.AccountSnapshotRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.inject.Inject

@Service
class AccountService @Inject constructor(private val userRepository: UserRepository,
                                         private val accountRepository: AccountRepository,
                                         private val accountAuditRepository: AccountSnapshotRepository) {

    fun getAccounts(userId: Long): List<Account> {
        return accountRepository.findByUserId(userId).toList().sortedBy { it.id }
    }

    fun addAccount(userId: Long, accountDto: Account): Account {
        val user = userRepository.findById(userId).get()
        val account = accountDto.copy(user = user)
        return accountRepository.save(account)
    }

    fun editAccount(id: Long, account: Account) {
        val savedAccount = getAccount(id)
        val editedAccount = savedAccount.copy(name = account.name,
                currency = account.currency,
                amount = account.amount,
                description = account.description)
        accountRepository.save(editedAccount)
    }

    fun addAccountSnapshot(accountId: Long, accountSnapshot: AccountSnapshot) {
        val account = getAccount(accountId)
        val new = accountSnapshot.copy(account = account,
                date = LocalDate.now()
        )
        accountAuditRepository.save(new)
    }

    fun updateAccountValue(accountId: Long, amount: Float) {
        val account = getAccount(accountId)
        val new = AccountSnapshot(account = account,
                amount = amount,
                date = LocalDate.now()
        )
        accountAuditRepository.save(new)
        val updatedAccount = account.copy(amount = amount)
        accountRepository.save(updatedAccount)
    }

    private fun getLatestAuditForAccount(accountId: Long): LocalDate? {
        return accountAuditRepository.getLatestSnapshotForAccount(accountId)
    }

    fun getAccount(id: Long): Account {
        return accountRepository.findById(id).get()
    }

//    todo view how to do this with different currencies
    fun getTotalAccountValue(userId: Long): Float {
        return getAccounts(userId).sumByDouble { it.amount.toDouble() }.toFloat()
    }

    fun editAudit(auditId: Long, accountSnapshot: AccountSnapshot) {
        val entry = accountAuditRepository.findById(auditId).get()
        val new = entry.copy(date = accountSnapshot.date,
                amount = accountSnapshot.amount
        )
        accountAuditRepository.save(new)
    }

    fun getHistory(accountId: Long): AccountHistory {
        val account = getAccount(accountId)
        val audits = accountAuditRepository.findByAccountId(accountId)
                .toList()
                .sortedBy { it.date }
        return AccountHistory(current = account, history = audits)
    }

}