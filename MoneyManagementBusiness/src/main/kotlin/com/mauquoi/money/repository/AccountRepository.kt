package com.mauquoi.money.repository

import com.mauquoi.money.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AccountRepository: JpaRepository<Account, Long> {

    fun findByUserId(userId: Long): Set<Account>
}