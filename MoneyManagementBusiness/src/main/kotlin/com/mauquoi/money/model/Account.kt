package com.mauquoi.money.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "account")
data class Account(@Id
                   @GeneratedValue(strategy = GenerationType.IDENTITY)
                   var id: Long? = null,
                   @Column(name = "name", nullable = false) @NotNull val name: String,
                   @Column(name = "amount", nullable = false) @NotNull val amount: Float,
                   @Column(name = "currency", nullable = false) @NotNull val currency: Currency,
                   @Column(name = "description") val description: String? = null,
                   @ManyToOne @JsonIgnore val user: User? = null,
                   @Transient var amountInPreferredCurrency: Float? = null
)