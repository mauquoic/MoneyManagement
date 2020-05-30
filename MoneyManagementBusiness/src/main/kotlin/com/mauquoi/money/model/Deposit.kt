package com.mauquoi.money.model

data class Deposit(val id: Int? = 1,
                   val name: String = "Name",
                   val amount: Int = 2000,
                   val currency: String = "GBP",
                   val description: String? = null
)