package com.mauquoi.money.controller

import com.mauquoi.money.business.service.DepositService
import com.mauquoi.money.const.URL
import com.mauquoi.money.model.Deposit
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping("/api/v1/users/{userId}/deposits")
class DepositController @Inject constructor(private val depositService: DepositService) {

    @GetMapping
    fun getDeposits(@PathVariable(URL.PathVariable.USER_ID) userId: Long): ResponseEntity<List<Deposit>> {
        return ResponseEntity.ok(depositService.getDeposits(userId))
    }

    @GetMapping("/{id}")
    fun getDeposit(@PathVariable("id") id: Long): ResponseEntity<Deposit> {
        return ResponseEntity.ok(depositService.getDeposit(id))
    }

    @PutMapping("/{id}")
    fun putDeposit(@PathVariable("id") id: Long,
                   @RequestBody deposit: Deposit): ResponseEntity<Deposit> {
        return ResponseEntity.ok(depositService.editDeposit(id, deposit))
    }

    @PostMapping()
    fun addDeposit(@PathVariable(URL.PathVariable.USER_ID) userId: Long,
                   @RequestBody deposit: Deposit): ResponseEntity<Deposit> {
        return ResponseEntity.ok(depositService.addDeposit(userId, deposit))
    }
}