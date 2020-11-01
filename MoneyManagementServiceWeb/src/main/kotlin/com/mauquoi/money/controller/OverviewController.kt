package com.mauquoi.money.controller

import com.mauquoi.money.business.error.PreferredCurrencyUnknownException
import com.mauquoi.money.business.service.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
@RequestMapping("/users/{userId}/overview")
class OverviewController @Inject constructor(private val stockService: StockService,
                                             private val accountService: AccountService,
                                             private val depositService: DepositService,
                                             private val currencyService: CurrencyService,
                                             private val userService: UserService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getOverview(@PathVariable("userId") userId: Long): ResponseEntity<Any> {
        val preferredCurrency = userService.getUser(userId).preferences?.currency
                ?: throw PreferredCurrencyUnknownException()
        val stocks = stockService.getPositions(userId)

        val accounts = accountService.getAccounts(userId)

        val stockOverview = currencyService.createOverviewItem(stocks, preferredCurrency)
        val accountOverview = currencyService.createOverviewItem(accounts, preferredCurrency)

        return ResponseEntity.ok(mapOf("stocks" to stockOverview, "accounts" to accountOverview))
    }

}