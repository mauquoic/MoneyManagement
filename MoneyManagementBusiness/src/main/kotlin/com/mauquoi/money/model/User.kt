package com.mauquoi.money.model

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "users")
data class User(@Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                var id: Long? = null,
                @Column(name = "email", nullable = false) @NotNull val email: String,
                @OneToMany(cascade = [CascadeType.ALL]) val deposits: Set<Deposit>,
                @OneToMany(cascade = [CascadeType.ALL]) val stocks: Set<Stock>,
                @OneToMany(cascade = [CascadeType.ALL]) val accounts: Set<Account>
)