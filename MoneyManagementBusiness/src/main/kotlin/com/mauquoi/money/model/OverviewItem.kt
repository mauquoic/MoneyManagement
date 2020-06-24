package com.mauquoi.money.model

import java.util.*

data class OverviewItem(val mainCurrencyValue: Float,
                        val mainCurrency: Currency,
                        val distribution: Map<Currency, Float>
)