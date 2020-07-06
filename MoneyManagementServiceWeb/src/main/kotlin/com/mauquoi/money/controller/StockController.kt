package com.mauquoi.money.controller

import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.const.URL.PathVariable.STOCK_SYMBOL
import com.mauquoi.money.const.URL.Shared.API_BASE
import com.mauquoi.money.const.URL.Stock.STOCK_QUOTE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
@RequestMapping(API_BASE)
class StockController @Inject constructor(private val stockService: StockService) {

    @GetMapping(STOCK_QUOTE)
    fun getStockValue(@PathVariable(STOCK_SYMBOL) symbol: String): ResponseEntity<Double> {
        return ResponseEntity.ok(stockService.getStockPrice(symbol))
    }

}