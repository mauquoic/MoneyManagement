package com.mauquoi.money.controller

import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.business.service.StockService
import com.mauquoi.money.const.URL.PathVariable.POSITION_ID
import com.mauquoi.money.const.URL.PathVariable.USER_ID
import com.mauquoi.money.const.URL.Position.BASE
import com.mauquoi.money.const.URL.Position.DIVIDEND
import com.mauquoi.money.const.URL.Position.TRANSACTION
import com.mauquoi.money.const.URL.Position.POSITIONS_BY_ID
import com.mauquoi.money.extension.fromDto
import com.mauquoi.money.extension.toDto
import com.mauquoi.money.model.DividendDto
import com.mauquoi.money.model.TransactionDto
import com.mauquoi.money.model.PositionDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(BASE)
class PositionController @Inject constructor(private val stockService: StockService,
                                             private val currencyService: CurrencyService) {

    @GetMapping
    fun getPositions(@PathVariable(USER_ID) userId: Long): ResponseEntity<List<PositionDto>> {
        return ResponseEntity.ok(stockService.getPositions(userId).map { it.toDto() })
    }

    @GetMapping(POSITIONS_BY_ID)
    fun getPosition(@PathVariable(POSITION_ID) id: Long): ResponseEntity<PositionDto> {
        return ResponseEntity.ok(stockService.getPosition(id).toDto())
    }

    @PutMapping(POSITIONS_BY_ID)
    fun editPosition(@PathVariable(POSITION_ID) id: Long,
                     @RequestBody positionDto: PositionDto): ResponseEntity<Nothing> {
        stockService.editPosition(id, positionDto.fromDto())
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun addPosition(@PathVariable(USER_ID) userId: Long,
                    @RequestBody positionDto: PositionDto): ResponseEntity<PositionDto> {
        currencyService.verifyCurrencyCompatibility(positionDto.stock.market, positionDto.stock.currency)
        val position = stockService.addPosition(userId, positionDto.fromDto())
        return ResponseEntity.status(HttpStatus.CREATED).body(position.toDto())
    }

    @PostMapping(TRANSACTION)
    fun addTransaction(@PathVariable(USER_ID) userId: Long,
                       @PathVariable(POSITION_ID) positionId: Long,
                       @RequestBody transaction: TransactionDto): ResponseEntity<PositionDto> {
        val position = stockService.addTransaction(positionId, transaction.fromDto())
        return ResponseEntity.status(HttpStatus.CREATED).body(position.toDto())
    }

    @PostMapping(DIVIDEND)
    fun addDividend(@PathVariable(USER_ID) userId: Long,
                    @PathVariable(POSITION_ID) positionId: Long,
                    @RequestBody dividend: DividendDto): ResponseEntity<PositionDto> {
        val position = stockService.addDividend(positionId, dividend.fromDto())
        return ResponseEntity.status(HttpStatus.CREATED).body(position.toDto())
    }
}
