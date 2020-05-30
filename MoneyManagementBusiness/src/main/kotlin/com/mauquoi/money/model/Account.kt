package com.mauquoi.money.model

data class Account(val id: Int? = 1,
                   val name: String = "Name",
                   val amount: Int = 2000,
                   val currency: String = "CNY",
                   val description: String? = null
)