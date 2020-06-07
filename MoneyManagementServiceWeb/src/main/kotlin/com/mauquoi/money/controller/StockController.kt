package com.mauquoi.money.controller

import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.const.URL
import com.mauquoi.money.model.Stock
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping("/api/v1/users/{userId}/stocks")
class StockController @Inject constructor(private val stockService: StockService) {

    @GetMapping
    fun getStocks(@PathVariable("userId") userId: Long): ResponseEntity<List<Stock>> {
        return ResponseEntity.ok(stockService.getStocks(userId))
    }

    @GetMapping("/{id}")
    fun getStock(@PathVariable("id") id: Long): ResponseEntity<Stock> {
        return ResponseEntity.ok(stockService.getStock(id))
    }

    @PutMapping("/{id}")
    fun putStock(@PathVariable("id") id: Long,
                 @RequestBody stock: Stock): ResponseEntity<Stock> {
        return ResponseEntity.ok(stockService.editStock(id, stock))
    }

    @PostMapping()
    fun addStock(@PathVariable(URL.PathVariable.USER_ID) userId: Long,
                 @RequestBody stock: Stock): ResponseEntity<Stock> {
        return ResponseEntity.ok(stockService.addStock(userId, stock))
    }
}