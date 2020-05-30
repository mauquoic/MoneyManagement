package com.mauquoi.money.controller

import com.mauquoi.money.model.Account
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController {

    val accounts = mutableListOf(Account(), Account(id = 2, description = "I have a description", currency = "USD"), Account(id = 3), Account(id = 4), Account(id = 5)
            , Account(id = 6), Account(id = 7)
    )



    @GetMapping
    fun getAccounts(): ResponseEntity<List<Account>> {
        return ResponseEntity.ok(accounts)
    }

    @GetMapping("/{id}")
    fun getAccount(@PathVariable("id") id: String): ResponseEntity<Account> {
        return ResponseEntity.ok(Account())
    }

    @PutMapping("/{id}")
    fun putAccount(@PathVariable("id") id: String,
                   @RequestBody account: Account): ResponseEntity<Account> {
        return ResponseEntity.ok(account);
    }

    @PostMapping()
    fun putAccount(@RequestBody account: Account): ResponseEntity<Account> {
        return ResponseEntity.ok(account);
    }
}