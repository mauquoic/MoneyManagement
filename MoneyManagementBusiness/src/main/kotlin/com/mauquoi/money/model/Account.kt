package com.mauquoi.money.model

import javax.persistence.*

@Entity
@Table(name = "account")
data class Account(@Id
                   @GeneratedValue(strategy = GenerationType.IDENTITY)
                   var id: Long? = null,
                   val name: String = "Name",
                   val amount: Int = 2000,
                   val currency: String = "CNY",
                   val description: String? = null
)