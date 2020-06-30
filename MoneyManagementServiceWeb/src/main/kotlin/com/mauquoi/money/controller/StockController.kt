package com.mauquoi.money.controller

import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.const.URL.PathVariable.MARKET
import com.mauquoi.money.const.URL.PathVariable.STOCK_SYMBOL
import com.mauquoi.money.const.URL.Shared.API_BASE
import com.mauquoi.money.const.URL.Stock.STOCKS_BY_MARKET
import com.mauquoi.money.const.URL.Stock.STOCK_NAME
import com.mauquoi.money.const.URL.Stock.STOCK_QUOTE
import com.mauquoi.money.extension.toDto
import com.mauquoi.money.model.ExchangeDto
import com.mauquoi.money.model.dto.FinnhubStockDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(API_BASE)
class StockController @Inject constructor(private val stockService: StockService,
                                          private val currencyService: CurrencyService) {

    @GetMapping(STOCK_QUOTE)
    fun getStockValue(@PathVariable(STOCK_SYMBOL) symbol: String): ResponseEntity<Double> {
        return ResponseEntity.ok(stockService.getStockPrice(symbol))
    }

    @PutMapping(STOCKS_BY_MARKET)
    fun updateEntireMarket(@PathVariable(MARKET) market: String): ResponseEntity<Nothing> {
        stockService.updateStockExchange(market)
        return ResponseEntity.noContent().build()
    }

    @GetMapping(STOCKS_BY_MARKET)
    fun getMarket(@PathVariable(MARKET) market: String): ResponseEntity<ExchangeDto> {
        val currencyInMarket = currencyService.getCurrencyForMarket(market)
        val stocks = stockService.getStockExchange(market).map { it.toDto() }
        return ResponseEntity.ok(ExchangeDto(currency = currencyInMarket, market = market, stocks = stocks))
    }

    @GetMapping(STOCK_NAME)
    fun getStockName(@PathVariable(MARKET) market: String,
                     @PathVariable(STOCK_SYMBOL) symbol: String): ResponseEntity<FinnhubStockDto> {
        return ResponseEntity.ok(stockService.getStockName(symbol, market))
    }
}