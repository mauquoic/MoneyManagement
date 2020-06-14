package com.mauquoi.money.model.dto

import java.time.LocalDate
import java.util.*
import kotlin.collections.LinkedHashMap

data class CurrencyLookupDto(val base: Currency, val date: LocalDate, val rates: Map<Currency, Float>)