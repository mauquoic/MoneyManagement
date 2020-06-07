package com.mauquoi.money.controller

import com.mauquoi.money.business.service.AccountService
import com.mauquoi.money.const.URL.Account.BASE
import com.mauquoi.money.const.URL.Account.ACCOUNT_BY_ID
import com.mauquoi.money.const.URL.PathVariable.ACCOUNT_ID
import com.mauquoi.money.const.URL.PathVariable.USER_ID
import com.mauquoi.money.model.Account
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(BASE)
class AccountController @Inject constructor(private val accountService: AccountService) {

    @GetMapping
    fun getAccounts(): ResponseEntity<List<Account>> {
        return ResponseEntity.ok(accountService.getAccounts())
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

    @PostMapping()
    fun addAccount(@PathVariable(USER_ID) userId: Long,
                   @RequestBody account: Account): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.addAccount(userId, account))
    }
}