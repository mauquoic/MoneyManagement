package com.mauquoi.money.repository.snapshot

import com.mauquoi.money.model.audit.AccountSnapshot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface AccountSnapshotRepository : JpaRepository<AccountSnapshot, Long> {

    fun findByAccountId(accountId: Long): Set<AccountSnapshot>

    @Query("select max(a.date) from AccountSnapshot a where a.account.id = :accountId")
    fun getLatestSnapshotForAccount(accountId: Long): LocalDate?
}