package com.mauquoi.money.controller

import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.const.URL.PathVariable.STOCK_ID
import com.mauquoi.money.const.URL.PathVariable.USER_ID
import com.mauquoi.money.const.URL.StockPosition.BASE
import com.mauquoi.money.const.URL.StockPosition.STOCKS_BY_ID
import com.mauquoi.money.model.StockPosition
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(BASE)
class StockPositionController @Inject constructor(private val stockService: StockService) {

    @GetMapping
    fun getStockPositions(@PathVariable(USER_ID) userId: Long): ResponseEntity<List<StockPosition>> {
        return ResponseEntity.ok(stockService.getStockPositions(userId))
    }

    @GetMapping(STOCKS_BY_ID)
    fun getStockPosition(@PathVariable(STOCK_ID) id: Long): ResponseEntity<StockPosition> {
        return ResponseEntity.ok(stockService.getStockPosition(id))
    }

    @PutMapping(STOCKS_BY_ID)
    fun editStockPosition(@PathVariable(STOCK_ID) id: Long,
                          @RequestBody stock: StockPosition): ResponseEntity<Nothing> {
        stockService.editStockPosition(id, stock)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun addStockPosition(@PathVariable(USER_ID) userId: Long,
                         @RequestBody stock: StockPosition): ResponseEntity<StockPosition> {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.addStockPosition(userId, stock))
    }
}