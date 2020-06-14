package com.mauquoi.money.controller

import com.mauquoi.money.business.service.AccountService
import com.mauquoi.money.business.service.DepositService
import com.mauquoi.money.business.service.StockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
@RequestMapping("/api/v1/users/{userId}/overview")
class OverviewController @Inject constructor(private val stockService: StockService,
                                             private val accountService: AccountService,
                                             private val depositService: DepositService) {

    data class Overview(val cash: Int, val assets: Int, val deposits: Int, val cryptos: Int){
        val total: Int = cash + assets + deposits + cryptos
    }

    @GetMapping
    fun getOverview(@PathVariable("userId") userId: Long): ResponseEntity<Overview> {
        val overview = Overview(cash = accountService.getTotalAccountValue(userId).toInt(),
                assets = stockService.getTotalStocksValue(userId).toInt(),
                deposits = depositService.getTotalDepositsValue(userId),
                cryptos = 0)
        return ResponseEntity.ok(overview)
    }

}