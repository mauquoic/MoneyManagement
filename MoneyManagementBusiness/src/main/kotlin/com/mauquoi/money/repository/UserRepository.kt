package com.mauquoi.money.repository

import com.mauquoi.money.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
}