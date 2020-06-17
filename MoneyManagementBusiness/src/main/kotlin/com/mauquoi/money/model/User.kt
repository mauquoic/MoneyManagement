package com.mauquoi.money.model

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "users")
data class User(@Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                var id: Long? = null,
                @Column(name = "email", nullable = false) @NotNull val email: String,
                @OneToOne(cascade = [CascadeType.ALL]) @JoinColumn(name = "preferences_id") val preferences: UserPreferences? = UserPreferences(),
                @OneToMany(cascade = [CascadeType.ALL]) val deposits: Set<Deposit> = emptySet(),
                @OneToMany(cascade = [CascadeType.ALL]) val stocks: Set<Stock> = emptySet(),
                @OneToMany(cascade = [CascadeType.ALL]) val accounts: Set<Account> = emptySet()
)

@Entity
@Table(name = "preferences")
data class UserPreferences(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        @Column(name = "language") val locale: Locale = Locale.UK,
        @Column(name = "currency") val currency: Currency = Currency.getInstance("EUR")
)