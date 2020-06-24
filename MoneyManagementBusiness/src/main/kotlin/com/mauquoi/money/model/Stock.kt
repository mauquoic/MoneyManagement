package com.mauquoi.money.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "stock")
data class Stock(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        @Column(name = "name", nullable = false) @NotNull val name: String,
        @Column(name = "symbol") @NotNull val symbol: String,
        @Column(name = "market") @NotNull val market: String,
        @Column(name = "currency", nullable = false) @NotNull val currency: Currency,
        @Transient var value: Double = 5.0
) {

    fun createSymbol(): String {
        return if (market != "US") {
            "$symbol.$market"
        } else {
            symbol
        }
    }
}
