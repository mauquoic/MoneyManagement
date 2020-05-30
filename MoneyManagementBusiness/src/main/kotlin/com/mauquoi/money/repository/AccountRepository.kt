package com.mauquoi.money.repository

import com.mauquoi.money.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository: JpaRepository<Account, Long>