package com.mauquoi.money.mapping

import com.mauquoi.money.model.CurrencyLookup
import com.mauquoi.money.model.Market
import com.mauquoi.money.model.Stock
import com.mauquoi.money.model.dto.CurrencyLookupDto
import com.mauquoi.money.model.dto.FinnhubStockDto
import java.util.*

fun CurrencyLookupDto.fromDto(): CurrencyLookup = CurrencyLookup(
        base = this.base,
        date = this.date,
        rates = this.rates
)

fun FinnhubStockDto.fromDto(market: String, markets: List<Market>): Stock {
    return Stock(
            name = this.description,
            market = market,
            symbol = MappingUtil.getSymbol(market, this.symbol),
            currency = if (this.currency.isNullOrBlank()) markets.first{ it.market == market}.currency else Currency.getInstance(this.currency),
            type = if (!this.type.isNullOrBlank()) this.type else null
    )
}