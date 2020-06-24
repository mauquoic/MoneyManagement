package com.mauquoi.money.controller

import com.mauquoi.money.business.service.DepositService
import com.mauquoi.money.const.URL.Deposit.ADD_DEPOSIT_AUDIT
import com.mauquoi.money.const.URL.Deposit.BASE
import com.mauquoi.money.const.URL.Deposit.DEPOSIT_BY_ID
import com.mauquoi.money.const.URL.Deposit.EDIT_DEPOSIT_AUDIT
import com.mauquoi.money.const.URL.Deposit.GET_DEPOSIT_HISTORY
import com.mauquoi.money.const.URL.Deposit.UPDATE_DEPOSIT
import com.mauquoi.money.const.URL.PathVariable.AUDIT_ID
import com.mauquoi.money.const.URL.PathVariable.DEPOSIT_ID
import com.mauquoi.money.const.URL.PathVariable.USER_ID
import com.mauquoi.money.model.Deposit
import com.mauquoi.money.model.audit.DepositSnapshot
import com.mauquoi.money.model.history.DepositHistory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(BASE)
class DepositController @Inject constructor(private val depositService: DepositService) {

    @GetMapping
    fun getDeposits(@PathVariable(USER_ID) userId: Long): ResponseEntity<List<Deposit>> {
        return ResponseEntity.ok(depositService.getDeposits(userId))
    }

    @GetMapping(DEPOSIT_BY_ID)
    fun getDeposit(@PathVariable(DEPOSIT_ID) id: Long): ResponseEntity<Deposit> {
        return ResponseEntity.ok(depositService.getDeposit(id))
    }

    @PutMapping(DEPOSIT_BY_ID)
    fun editDeposit(@PathVariable(DEPOSIT_ID) id: Long,
                    @RequestBody deposit: Deposit): ResponseEntity<Deposit> {
        depositService.editDeposit(id, deposit)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun addDeposit(@PathVariable(USER_ID) userId: Long,
                   @RequestBody deposit: Deposit): ResponseEntity<Deposit> {
        return ResponseEntity.status(HttpStatus.CREATED).body(depositService.addDeposit(userId, deposit))
    }

    @PostMapping(UPDATE_DEPOSIT)
    fun updateDeposit(@PathVariable(DEPOSIT_ID) depositId: Long,
                      @RequestParam(name = "amount", required = true) amount: Double): ResponseEntity<Nothing> {
        depositService.updateDepositValue(depositId, amount)
        return ResponseEntity.noContent().build()
    }

    @PostMapping(ADD_DEPOSIT_AUDIT)
    fun addDepositAudit(@PathVariable(DEPOSIT_ID) depositId: Long,
                        @RequestBody depositSnapshot: DepositSnapshot): ResponseEntity<Nothing> {
        depositService.addDepositSnapshot(depositId, depositSnapshot = depositSnapshot)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping(EDIT_DEPOSIT_AUDIT)
    fun editDepositAudit(@PathVariable(AUDIT_ID) auditId: Long,
                         @RequestBody depositSnapshot: DepositSnapshot): ResponseEntity<Nothing> {
        depositService.editAudit(auditId, depositSnapshot)
        return ResponseEntity.noContent().build()
    }

    @GetMapping(GET_DEPOSIT_HISTORY)
    fun getDepositHistory(@PathVariable(DEPOSIT_ID) depositId: Long): ResponseEntity<DepositHistory> {
        val history = depositService.getHistory(depositId)
        return ResponseEntity.ok(history)
    }
}