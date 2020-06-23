package com.mauquoi.money.repository.snapshot

import com.mauquoi.money.model.audit.DepositSnapshot
import org.springframework.data.jpa.repository.JpaRepository

interface DepositSnapshotRepository : JpaRepository<DepositSnapshot, Long> {

    fun findByDepositId(depositId: Long): Set<DepositSnapshot>
}