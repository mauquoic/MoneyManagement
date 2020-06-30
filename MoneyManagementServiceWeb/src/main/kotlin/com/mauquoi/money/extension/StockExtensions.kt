package com.mauquoi.money.extension

import com.mauquoi.money.model.*
import java.math.RoundingMode

fun Stock.toDto(): StockDto = StockDto(name = this.name,
        symbol = this.symbol,
        lookup = this.lookup!!)

fun Stock.toDetailsDto(): StockDetailsDto = StockDetailsDto(name = this.name,
        symbol = this.symbol,
        lookup = this.lookup!!,
        market = this.market,
        currency = this.currency)

fun StockPosition.toDto(): StockPositionDto = StockPositionDto(id = this.id,
        stock = this.stock.toDetailsDto(),
        positions = this.positions.map { it.toDto() },
        dividends = this.dividends.map { it.toDto() },
        description = this.description,
        value = this.calculateValue().toBigDecimal().setScale(2, RoundingMode.HALF_UP))

fun Position.toDto(): PositionDto = PositionDto(id = this.id,
        amount = this.amount,
        purchasePrice = this.purchasePrice,
        fees = this.fees,
        date = this.date)

fun Dividend.toDto(): DividendDto = DividendDto(id = this.id,
        date = this.date,
        amount = this.totalAmount)
