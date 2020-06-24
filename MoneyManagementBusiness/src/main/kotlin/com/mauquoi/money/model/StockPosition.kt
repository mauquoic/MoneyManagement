package com.mauquoi.money.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "stock_position")
data class StockPosition(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        @ManyToOne val stock: Stock,
        @OneToMany(cascade = [CascadeType.ALL]) val positions: List<Position> = emptyList(),
        @OneToMany(cascade = [CascadeType.ALL]) val dividends: List<Dividend> = emptyList(),
        @Column(name = "description") val description: String? = null,
        @ManyToOne @JsonIgnore
        val user: User? = null,
        @Transient var value: Double = 5.0
) : ValueItem, CurrencyItem {

    override fun currency(): Currency {
        return stock.currency
    }

    override fun value(): Double {
        return calculateValue()
    }

    val totalCosts = calculateCosts()
    val totalReturn = calculateValue() + dividends.fold(0.0) { acc, dividend -> acc + dividend.totalAmount }

    fun calculateValue(): Double {
        return positions.sumBy { it.amount }.times(value)
    }

    fun calculateCosts(): Double {
        return positions.fold(0.0) { acc, position -> acc + position.calculateCosts() }
    }
}


@Entity
data class Position(@Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    var id: Long? = null,
                    @Column(name = "amount", nullable = false) @NotNull val amount: Int,
                    @Column(name = "purchasePrice", nullable = false) @NotNull val purchasePrice: Double,
                    @Column(name = "fees", nullable = false) @NotNull val fees: Double = 0.0,
                    @Column(name = "date", nullable = false) @NotNull val date: LocalDate
) {
    fun calculateCosts(): Double {
        return amount * purchasePrice + fees
    }
}

@Entity
data class Dividend(@Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    var id: Long? = null,
                    @Column(name = "totalAmount", nullable = false) @NotNull val totalAmount: Double,
                    @Column(name = "date", nullable = false) @NotNull val date: LocalDate? = LocalDate.now()
)