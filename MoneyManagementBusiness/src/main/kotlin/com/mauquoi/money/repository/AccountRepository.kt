package com.mauquoi.money.repository

import com.mauquoi.money.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AccountRepository: JpaRepository<Account, Long> {

    @Query("select a from Account a where a.user.id = :userId")
    fun findAllBelongingToUser(userId: Long): Set<Account>
}