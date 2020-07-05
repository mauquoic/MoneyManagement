package com.mauquoi.money.model.dto

import java.time.LocalDate
import java.util.*

data class CurrencyLookupDto(val base: Currency,
                             val date: LocalDate,
                             val rates: Map<Currency, Double>)