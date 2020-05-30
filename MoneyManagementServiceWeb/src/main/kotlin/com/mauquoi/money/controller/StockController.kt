package com.mauquoi.money.controller

import com.mauquoi.money.model.Position
import com.mauquoi.money.model.Stock
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/stocks")
class StockController {

    val stocks = mutableListOf(Stock(), Stock(id = 2, description = "I have a position",
            positions = listOf(Position(id = 2))), Stock(id = 3), Stock(id = 4), Stock(id = 5)
            , Stock(id = 6), Stock(id = 7)
    )


    @GetMapping
    fun getStocks(): ResponseEntity<List<Stock>> {
        return ResponseEntity.ok(stocks)
    }

    @GetMapping("/{id}")
    fun getStock(@PathVariable("id") id: String): ResponseEntity<Stock> {
        return ResponseEntity.ok(Stock())
    }

    @PutMapping("/{id}")
    fun putStock(@PathVariable("id") id: String,
                 @RequestBody stock: Stock): ResponseEntity<Stock> {
        return ResponseEntity.ok(stock);
    }

    @PostMapping()
    fun addStock(@RequestBody stock: Stock): ResponseEntity<Stock> {
        return ResponseEntity.ok(stock);
    }
}