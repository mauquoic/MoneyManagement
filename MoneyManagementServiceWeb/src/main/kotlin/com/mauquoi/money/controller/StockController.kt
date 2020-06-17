package com.mauquoi.money.controller

import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.const.URL.PathVariable.MARKET
import com.mauquoi.money.const.URL.PathVariable.STOCK_ID
import com.mauquoi.money.const.URL.PathVariable.STOCK_SYMBOL
import com.mauquoi.money.const.URL.PathVariable.USER_ID
import com.mauquoi.money.const.URL.Shared.API_BASE
import com.mauquoi.money.const.URL.Stock.STOCKS
import com.mauquoi.money.const.URL.Stock.STOCKS_BY_ID
import com.mauquoi.money.const.URL.Stock.STOCK_NAME
import com.mauquoi.money.const.URL.Stock.STOCK_QUOTE
import com.mauquoi.money.model.Stock
import com.mauquoi.money.model.dto.FinnhubStockDto
import com.mauquoi.money.model.dto.QuoteDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(API_BASE)
class StockController @Inject constructor(private val stockService: StockService) {

    @GetMapping(STOCKS)
    fun getStocks(@PathVariable(USER_ID) userId: Long): ResponseEntity<List<Stock>> {
        return ResponseEntity.ok(stockService.getStocks(userId))
    }

    @GetMapping(STOCKS_BY_ID)
    fun getStock(@PathVariable(STOCK_ID) id: Long): ResponseEntity<Stock> {
        return ResponseEntity.ok(stockService.getStock(id))
    }

    @PutMapping(STOCKS_BY_ID)
    fun putStock(@PathVariable(STOCK_ID) id: Long,
                 @RequestBody stock: Stock): ResponseEntity<Stock> {
        return ResponseEntity.ok(stockService.editStock(id, stock))
    }

    @PostMapping(STOCKS)
    fun addStock(@PathVariable(USER_ID) userId: Long,
                 @RequestBody stock: Stock): ResponseEntity<Stock> {
        return ResponseEntity.ok(stockService.addStock(userId, stock))
    }

    @GetMapping(STOCK_QUOTE)
    fun getStockValue(@PathVariable(STOCK_SYMBOL) symbol: String): ResponseEntity<QuoteDto> {
        return ResponseEntity.ok(stockService.getStockPrice(symbol))
    }

    @GetMapping(STOCK_NAME)
    fun getStockValue(@PathVariable(MARKET) market: String,
                      @PathVariable(STOCK_SYMBOL) symbol: String): ResponseEntity<FinnhubStockDto> {
        return ResponseEntity.ok(stockService.getStockName(symbol, market))
    }
}