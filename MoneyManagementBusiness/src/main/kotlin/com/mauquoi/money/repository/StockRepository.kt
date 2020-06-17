package com.mauquoi.money.repository

import com.mauquoi.money.model.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StockRepository: JpaRepository<Stock, Long> {

    fun findByUserId(userId: Long): Set<Stock>
}