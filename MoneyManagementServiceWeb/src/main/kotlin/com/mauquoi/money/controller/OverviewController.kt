package com.mauquoi.money.controller

import com.mauquoi.money.business.error.PreferredCurrencyUnknownException
import com.mauquoi.money.business.service.*
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
                                             private val depositService: DepositService,
                                             private val currencyService: CurrencyService,
                                             private val userService: UserService) {

    data class Overview(val cash: Int, val assets: Int, val deposits: Int, val cryptos: Int) {
        val total: Int = cash + assets + deposits + cryptos
    }

    @GetMapping
    fun getOverview(@PathVariable("userId") userId: Long): ResponseEntity<Any> {
        val preferredCurrency = userService.getUser(userId).preferences?.currency
                ?: throw PreferredCurrencyUnknownException()
        val stocks = stockService.getStockPositions(userId)

        val accounts = accountService.getAccounts(userId)

        val stockOverview = currencyService.createOverviewItem(stocks, preferredCurrency)
        val accountOverview = currencyService.createOverviewItem(accounts, preferredCurrency)

        return ResponseEntity.ok(mapOf("stocks" to stockOverview, "accounts" to accountOverview))
    }

}