package com.mauquoi.money.model.dto

import com.mauquoi.money.model.CurrencyLookup

fun CurrencyLookupDto.fromDto(): CurrencyLookup = CurrencyLookup(
        base = this.base,
        date = this.date,
        rates = this.rates
)