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
        @OneToMany(cascade = [CascadeType.ALL]) val positions: List<Position> = emptyList(),
        @OneToMany(cascade = [CascadeType.ALL]) val dividends: List<Dividend> = emptyList(),
        @Column(name = "description") val description: String? = null,
        @ManyToOne @JsonIgnore val user: User? = null,
        @Transient var value: Float = 5f
) {
    val totalValue = calculateValue()
    val totalCosts = calculateCosts()
    val totalReturn = calculateValue() + dividends.fold(0f) { acc, dividend -> acc + dividend.totalAmount }

    fun calculateValue(): Float {
        return positions.sumBy { it.amount }.times(value)
    }

    fun calculateCosts(): Float {
        return positions.fold(0f) { acc, position -> acc + position.calculateCosts() }
    }

    fun createSymbol(): String {
        return if (market != "US") {
            "$symbol.$market"
        } else {
            symbol
        }
    }
}

@Entity
data class Position(@Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    var id: Long? = null,
                    @Column(name = "amount", nullable = false) @NotNull val amount: Int,
                    @Column(name = "purchasePrice", nullable = false) @NotNull val purchasePrice: Float,
                    @Column(name = "fees", nullable = false) @NotNull val fees: Float = 0f,
                    @Column(name = "date", nullable = false) @NotNull val date: LocalDate
) {
    fun calculateCosts(): Float {
        return amount * purchasePrice + fees
    }
}

@Entity
data class Dividend(@Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    var id: Long? = null,
                    @Column(name = "totalAmount", nullable = false) @NotNull val totalAmount: Float,
                    @Column(name = "date", nullable = false) @NotNull val date: LocalDate? = LocalDate.now()
)