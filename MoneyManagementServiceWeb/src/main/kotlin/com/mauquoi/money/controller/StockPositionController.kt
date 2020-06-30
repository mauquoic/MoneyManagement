package com.mauquoi.money.controller

import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.const.URL.PathVariable.STOCK_POSITION_ID
import com.mauquoi.money.const.URL.PathVariable.USER_ID
import com.mauquoi.money.const.URL.StockPosition.BASE
import com.mauquoi.money.const.URL.StockPosition.STOCK_POSITIONS_BY_ID
import com.mauquoi.money.extension.fromDto
import com.mauquoi.money.extension.toDto
import com.mauquoi.money.model.StockPositionDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(BASE)
class StockPositionController @Inject constructor(private val stockService: StockService,
                                                  private val currencyService: CurrencyService) {

    @GetMapping
    fun getStockPositions(@PathVariable(USER_ID) userId: Long): ResponseEntity<List<StockPositionDto>> {
        return ResponseEntity.ok(stockService.getStockPositions(userId).map { it.toDto() })
    }

    @GetMapping(STOCK_POSITIONS_BY_ID)
    fun getStockPosition(@PathVariable(STOCK_POSITION_ID) id: Long): ResponseEntity<StockPositionDto> {
        return ResponseEntity.ok(stockService.getStockPosition(id).toDto())
    }

    @PutMapping(STOCK_POSITIONS_BY_ID)
    fun editStockPosition(@PathVariable(STOCK_POSITION_ID) id: Long,
                          @RequestBody stockPosition: StockPositionDto): ResponseEntity<Nothing> {
        stockService.editStockPosition(id, stockPosition.fromDto())
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun addStockPosition(@PathVariable(USER_ID) userId: Long,
                         @RequestBody stockPosition: StockPositionDto): ResponseEntity<StockPositionDto> {
        currencyService.verifyCurrencyCompatibility(stockPosition.stock.market, stockPosition.stock.currency)
        val stockPosition = stockService.addStockPosition(userId, stockPosition.fromDto())
        return ResponseEntity.status(HttpStatus.CREATED).body(stockPosition.toDto())
    }
}
