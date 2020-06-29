package com.mauquoi.money.model

import java.math.BigDecimal
import java.util.*

data class OverviewItem(val mainCurrencyValue: BigDecimal,
                        val mainCurrency: Currency,
                        val distribution: Map<Currency, BigDecimal>
)