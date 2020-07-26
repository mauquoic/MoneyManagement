package com.mauquoi.money.model

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
        @Column(name = "lookup", nullable = false, unique = true) @NotNull var lookup: String? = null,
        @Column(name = "type") val type: String? = null,
        @Transient var value: Double = 5.0
) {
    init {
        this.lookup = createLookup()
    }

    fun createLookup(): String {
        return if (market != "US") {
            "$symbol.$market"
        } else {
            symbol
        }
    }
}
