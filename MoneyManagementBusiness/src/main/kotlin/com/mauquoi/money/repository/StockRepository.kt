package com.mauquoi.money.repository

import com.mauquoi.money.model.Stock
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StockRepository: JpaRepository<Stock, Long> {
    fun findByLookup(createLookup: String): Optional<Stock>
}