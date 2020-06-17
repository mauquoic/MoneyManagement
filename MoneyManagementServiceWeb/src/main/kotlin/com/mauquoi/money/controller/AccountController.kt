package com.mauquoi.money.controller

import com.mauquoi.money.business.service.AccountService
import com.mauquoi.money.const.URL.Account.ACCOUNT_BY_ID
import com.mauquoi.money.const.URL.Account.ADD_ACCOUNT_AUDIT
import com.mauquoi.money.const.URL.Account.BASE
import com.mauquoi.money.const.URL.Account.EDIT_ACCOUNT_AUDIT
import com.mauquoi.money.const.URL.Account.GET_ACCOUNT_HISTORY
import com.mauquoi.money.const.URL.Account.UPDATE_ACCOUNT
import com.mauquoi.money.const.URL.PathVariable.ACCOUNT_ID
import com.mauquoi.money.const.URL.PathVariable.AUDIT_ID
import com.mauquoi.money.const.URL.PathVariable.USER_ID
import com.mauquoi.money.model.Account
import com.mauquoi.money.model.audit.AccountSnapshot
import com.mauquoi.money.model.history.AccountHistory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(BASE)
class AccountController @Inject constructor(private val accountService: AccountService) {

    @GetMapping
    fun getAccounts(@PathVariable(USER_ID) userId: Long): ResponseEntity<List<Account>> {
        return ResponseEntity.ok(accountService.getAccounts(userId))
    }

    @PostMapping
    fun addAccount(@PathVariable(USER_ID) userId: Long,
                   @RequestBody account: Account): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.addAccount(userId, account))
    }

    @GetMapping(ACCOUNT_BY_ID)
    fun getAccount(@PathVariable(USER_ID) userId: Long,
                   @PathVariable(ACCOUNT_ID) accountId: Long): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.getAccount(accountId))
    }

    @PutMapping(ACCOUNT_BY_ID)
    fun editAccount(@PathVariable(ACCOUNT_ID) accountId: Long,
                    @RequestBody account: Account): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.editAccount(accountId, account))
    }

    @PutMapping(UPDATE_ACCOUNT)
    fun updateAccount(@PathVariable(ACCOUNT_ID) accountId: Long,
                      @RequestParam(name = "amount", required = true) amount: Float): ResponseEntity<Nothing> {
        accountService.updateAccountValue(accountId, amount)
        return ResponseEntity.noContent().build()
    }

    @PostMapping(ADD_ACCOUNT_AUDIT)
    fun addAccountAudit(@PathVariable(ACCOUNT_ID) accountId: Long,
                        @RequestBody accountSnapshot: AccountSnapshot): ResponseEntity<Nothing> {
        accountService.addAccountSnapshot(accountId, accountSnapshot = accountSnapshot)
        return ResponseEntity.noContent().build()
    }

    @PutMapping(EDIT_ACCOUNT_AUDIT)
    fun editAccountAudit(@PathVariable(AUDIT_ID) auditId: Long,
                         @RequestBody accountSnapshot: AccountSnapshot): ResponseEntity<Nothing> {
        accountService.editAudit(auditId, accountSnapshot)
        return ResponseEntity.noContent().build()
    }

    @GetMapping(GET_ACCOUNT_HISTORY)
    fun getAccountHistory(@PathVariable(ACCOUNT_ID) accountId: Long): ResponseEntity<AccountHistory> {
        val history = accountService.getHistory(accountId)
        return ResponseEntity.ok(history)
    }

}