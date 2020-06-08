package com.mauquoi.money.business.service

import com.mauquoi.money.model.Account
import com.mauquoi.money.model.audit.AccountAudit
import com.mauquoi.money.model.history.AccountHistory
import com.mauquoi.money.repository.AccountRepository
import com.mauquoi.money.repository.UserRepository
import com.mauquoi.money.repository.audit.AccountAuditRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.inject.Inject

@Service
class AccountService @Inject constructor(private val userRepository: UserRepository,
                                         private val accountRepository: AccountRepository,
                                         private val accountAuditRepository: AccountAuditRepository) {

    fun getAccounts(userId: Long): List<Account> {
        return accountRepository.findAllBelongingToUser(userId).toList().sortedBy { it.id }
    }

    fun addAccount(userId: Long, accountDto: Account): Account {
        val user = userRepository.findById(userId).get()
        val account = accountDto.copy(user = user)
        return accountRepository.save(account)
    }

    fun editAccount(id: Long, account: Account): Account {
        val savedAccount = getAccount(id)
        val editedAccount = savedAccount.copy(name = account.name,
                currency = account.currency,
                amount = account.amount,
                description = account.description)
        return accountRepository.save(editedAccount)
    }

    fun updateAccountValue(accountId: Long, amount: Int? = null, accountAudit: AccountAudit? = null) {
        val account = getAccount(accountId)
        val new = AccountAudit(account = account,
                amount = accountAudit?.amount ?: account.amount,
                from = accountAudit?.from ?: getLatestAuditForAccount(accountId) ?: LocalDate.now(),
                to = accountAudit?.to ?: LocalDate.now()
        )
        accountAuditRepository.save(new)
        amount?.let {
            val updatedAccount = account.copy(amount = amount)
            accountRepository.save(updatedAccount)
        }
    }

    private fun getLatestAuditForAccount(accountId: Long): LocalDate? {
        return accountAuditRepository.getLatestAuditForAccount(accountId)
    }

    fun getAccount(id: Long): Account {
        return accountRepository.findById(id).get()
    }

    fun getTotalAccountValue(userId: Long): Int {
        return getAccounts(userId).sumBy { it.amount }
    }

    fun editAudit(auditId: Long, accountAudit: AccountAudit) {
        val entry = accountAuditRepository.findById(auditId).get()
        val new = entry.copy(from = accountAudit.from,
                to = accountAudit.to,
                amount = accountAudit.amount
        )
        accountAuditRepository.save(new)
    }

    fun getHistory(accountId: Long): AccountHistory {
        val account = getAccount(accountId)
        val audits = accountAuditRepository.getAuditsForAccount(accountId)
                .toList()
                .sortedBy { it.from }
        return AccountHistory(current = account, history = audits)
    }

}