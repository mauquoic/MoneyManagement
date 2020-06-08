package com.mauquoi.money.repository.audit

import com.mauquoi.money.model.audit.AccountAudit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface AccountAuditRepository : JpaRepository<AccountAudit, Long> {

    @Query("select a from AccountAudit a where a.account.id = :accountId")
    fun getAuditsForAccount(accountId: Long): Set<AccountAudit>

    @Query("select max(a.to) from AccountAudit a where a.account.id = :accountId")
    fun getLatestAuditForAccount(accountId: Long): LocalDate?
}