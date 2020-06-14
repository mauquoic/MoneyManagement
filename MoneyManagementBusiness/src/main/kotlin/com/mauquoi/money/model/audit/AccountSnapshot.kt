package com.mauquoi.money.model.audit

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mauquoi.money.model.Account
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "account_snapshot")
data class AccountSnapshot(@Id
                           @GeneratedValue(strategy = GenerationType.IDENTITY)
                           var id: Long? = null,
                           @ManyToOne @JsonIgnore val account: Account?,
                           @Column(name = "amount", nullable = false) val amount: Int,
                           @Column(name = "date", nullable = false) val date: LocalDate
)