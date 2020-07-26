package com.mauquoi.money.extension

import com.mauquoi.money.model.*

fun PositionDto.fromDto(): Position = Position(
        id = this.id,
        stock = this.stock.fromDto(),
        transactions = this.transactions.map { it.fromDto() },
        dividends = this.dividends.map { it.fromDto() },
        description = this.description
)

fun DividendDto.fromDto(): Dividend = Dividend(
        id = this.id,
        totalAmount = this.amount,
        date = this.date
)

fun TransactionDto.fromDto(): Transaction = Transaction(
        id = this.id,
        amount = this.amount,
        date = this.date,
        fees = this.fees,
        purchasePrice = this.purchasePrice
)

fun StockDetailsDto.fromDto(): Stock = Stock(
        name = this.name,
        symbol = this.symbol,
        market = this.market,
        currency = this.currency,
        type = this.type
)
