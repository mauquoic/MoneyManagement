package com.mauquoi.money.model

import java.util.*

data class OverviewItem(val mainCurrencyValue: Double,
                        val mainCurrency: Currency,
                        val distribution: Map<Currency, Double>
)