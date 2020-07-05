package com.mauquoi.money.repository

import com.mauquoi.money.model.Position
import org.springframework.data.jpa.repository.JpaRepository

interface PositionRepository : JpaRepository<Position, Long> {
    fun findByUserId(userId: Long): Set<Position>
}