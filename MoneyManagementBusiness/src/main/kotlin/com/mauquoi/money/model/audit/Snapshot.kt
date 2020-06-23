package com.mauquoi.money.model.audit

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mauquoi.money.model.Account
import com.mauquoi.money.model.Deposit
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "account_snapshot")
data class AccountSnapshot(@Id
                           @GeneratedValue(strategy = GenerationType.IDENTITY)
                           var id: Long? = null,
                           @ManyToOne @JsonIgnore val account: Account?,
                           @Column(name = "amount", nullable = false) val amount: Float,
                           @Column(name = "date", nullable = false) val date: LocalDate
)

@Entity
@Table(name = "deposit_snapshot")
data class DepositSnapshot(@Id
                           @GeneratedValue(strategy = GenerationType.IDENTITY)
                           var id: Long? = null,
                           @ManyToOne @JsonIgnore val deposit: Deposit?,
                           @Column(name = "amount", nullable = false) val amount: Float,
                           @Column(name = "date", nullable = false) val date: LocalDate
)