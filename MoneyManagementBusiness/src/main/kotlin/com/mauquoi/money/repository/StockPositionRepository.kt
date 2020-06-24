package com.mauquoi.money.repository

import com.mauquoi.money.model.StockPosition
import org.springframework.data.jpa.repository.JpaRepository

interface StockPositionRepository : JpaRepository<StockPosition, Long> {
    fun findByUserId(userId: Long): Set<StockPosition>
}