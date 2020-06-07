package com.mauquoi.money.repository

import com.mauquoi.money.model.Deposit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DepositRepository : JpaRepository<Deposit, Long> {

    @Query("select d from Deposit d where d.user.id = :userId")
    fun findAllBelongingToUser(userId: Long): Set<Deposit>
}