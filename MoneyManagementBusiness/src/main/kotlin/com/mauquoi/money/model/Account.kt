package com.mauquoi.money.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "account")
data class Account(@Id
                   @GeneratedValue(strategy = GenerationType.IDENTITY)
                   var id: Long? = null,
                   @Column(name = "name", nullable = false) @NotNull val name: String = "Name",
                   @Column(name = "amount", nullable = false) @NotNull val amount: Int = 2000,
                   @Column(name = "currency", nullable = false) @NotNull val currency: String = "CNY",
                   @Column(name = "description") val description: String? = null,
                   @ManyToOne @JsonIgnore val user: User? = null
)