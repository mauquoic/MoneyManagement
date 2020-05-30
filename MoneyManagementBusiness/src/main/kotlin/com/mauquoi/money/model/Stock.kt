package com.mauquoi.money.model

import java.time.LocalDate

data class Stock(val id: Int? = 1,
                 val name: String = "Name",
                 val shortForm: String = "XYZ",
                 val value: Float = 43.3f,
                 val currency: String = "CNY",
                 val positions: List<Position> = emptyList(),
                 val dividends: List<Dividend> = emptyList(),
                 val description: String? = "desc"
)

data class Position(val id: Int? = 1,
                    val amount: Int = 10,
                    val purchasePrice: Float = 20.8f,
                    val fees: Float = 20.8f,
                    val date: LocalDate? = null
)

data class Dividend(val id: Int? = 1,
                    val perItem: Float?,
                    val totalAmount: Float?,
                    val date: LocalDate? = LocalDate.now()
)