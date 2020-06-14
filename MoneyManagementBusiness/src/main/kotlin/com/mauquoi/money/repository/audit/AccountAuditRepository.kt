package com.mauquoi.money.repository.audit

import com.mauquoi.money.model.audit.AccountSnapshot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface AccountAuditRepository : JpaRepository<AccountSnapshot, Long> {

    @Query("select a from AccountSnapshot a where a.account.id = :accountId")
    fun getAuditsForAccount(accountId: Long): Set<AccountSnapshot>

    @Query("select max(a.date) from AccountSnapshot a where a.account.id = :accountId")
    fun getLatestAuditForAccount(accountId: Long): LocalDate?
}