package com.mauquoi.money.controller

import com.mauquoi.money.model.Deposit
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/deposits")
class DepositController {

    val deposits = mutableListOf(Deposit(), Deposit(id = 2, description = "I have a description", currency = "USD"), Deposit(id = 3), Deposit(id = 4), Deposit(id = 5)
            , Deposit(id = 6), Deposit(id = 7)
    )



    @GetMapping
    fun getDeposits(): ResponseEntity<List<Deposit>> {
        return ResponseEntity.ok(deposits)
    }

    @GetMapping("/{id}")
    fun getDeposit(@PathVariable("id") id: String): ResponseEntity<Deposit> {
        return ResponseEntity.ok(Deposit())
    }

    @PutMapping("/{id}")
    fun putDeposit(@PathVariable("id") id: String,
                   @RequestBody deposit: Deposit): ResponseEntity<Deposit> {
        return ResponseEntity.ok(deposit);
    }

    @PostMapping()
    fun putDeposit(@RequestBody deposit: Deposit): ResponseEntity<Deposit> {
        return ResponseEntity.ok(deposit);
    }
}