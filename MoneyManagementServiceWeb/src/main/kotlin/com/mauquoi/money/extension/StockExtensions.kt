package com.mauquoi.money.extension

import com.mauquoi.money.model.Stock
import com.mauquoi.money.model.StockDto

fun Stock.toDto(): StockDto = StockDto(name = this.name, symbol = this.symbol, lookup = this.lookup!!)