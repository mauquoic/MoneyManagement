package com.mauquoi.money.model.audit

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mauquoi.money.model.Account
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "account_audit")
data class AccountAudit(@Id
                        @GeneratedValue(strategy = GenerationType.IDENTITY)
                        var id: Long? = null,
                        @ManyToOne @JsonIgnore val account: Account?,
                        @Column(name = "amount", nullable = false) val amount: Int,
                        @Column(name = "from_date", nullable = false) val from: LocalDate,
                        @Column(name = "to_date", nullable = false) val to: LocalDate = LocalDate.now()
)