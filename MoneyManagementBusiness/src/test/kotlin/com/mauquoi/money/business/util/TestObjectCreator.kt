package com.mauquoi.money.business.util

import com.mauquoi.money.model.Account
import com.mauquoi.money.model.User
import com.mauquoi.money.model.UserPreferences
import com.mauquoi.money.model.audit.AccountSnapshot
import java.time.LocalDate
import java.util.*

object TestObjectCreator {

    fun createUser(): User {
        return User(id = 1L, email = "user@email.com", preferences = null)
    }

    fun createUserWithPreferences(): User {
        return User(id = 1L, email = "user@email.com", preferences = UserPreferences(1L, Locale.GERMAN, Currency.getInstance("USD")))
    }

    fun createAccount(): Account {
        return createAccounts()[0]
    }

    fun createAccountSnapshot(): AccountSnapshot {
        return createAccountSnapshots()[0]
    }

    fun createAccountSnapshotWithoutAccount(): AccountSnapshot {
        return AccountSnapshot(amount = 250f, account = null, date = LocalDate.of(2020, 1, 1))
    }

    fun createAccounts(): List<Account> {
        return listOf(Account(id = 1L, name = "Account", currency = Currency.getInstance("CHF"), description = "Description", amount = 100F),
                Account(id = 2L, name = "2ndAccount", currency = Currency.getInstance("EUR"), description = "2ndDescription", amount = 200F))
    }

    fun createAccountSnapshots(): List<AccountSnapshot> {
        val account = createAccount()
        return listOf(
                AccountSnapshot(id = 1L, account = account, amount = 250f, date = LocalDate.of(2020, 1, 1)),
                AccountSnapshot(id = 2L, account = account, amount = 350f, date = LocalDate.of(2020, 3, 1))
        )
    }
}