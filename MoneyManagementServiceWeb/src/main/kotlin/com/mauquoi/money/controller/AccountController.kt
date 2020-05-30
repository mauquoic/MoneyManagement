package com.mauquoi.money.controller

import com.mauquoi.money.business.service.AccountService
import com.mauquoi.money.model.Account
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping("/api/accounts")
class AccountController @Inject constructor(private val accountService: AccountService) {

    val accounts = mutableListOf(Account(), Account(id = 2, description = "I have a description", currency = "USD"), Account(id = 3), Account(id = 4), Account(id = 5)
            , Account(id = 6), Account(id = 7)
    )

    @GetMapping
    fun getAccounts(): ResponseEntity<List<Account>> {
        return ResponseEntity.ok(accountService.getAccounts())
    }

    @GetMapping("/{id}")
    fun getAccount(@PathVariable("id") id: String): ResponseEntity<Account> {
        return ResponseEntity.ok(Account())
    }

    @PutMapping("/{id}")
    fun editAccount(@PathVariable("id") id: Long,
                   @RequestBody account: Account): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.editAccount(id, account));
    }

    @PostMapping()
    fun addAccount(@RequestBody account: Account): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.addAccount(account));
    }
}