package com.mauquoi.money.util

import com.mauquoi.money.model.*
import com.mauquoi.money.model.audit.AccountSnapshot
import com.mauquoi.money.model.audit.DepositSnapshot
import java.time.LocalDate
import java.util.*

object TestObjectCreator {

    fun usd(): Currency = Currency.getInstance("USD")
    fun chf(): Currency = Currency.getInstance("CHF")

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
        return AccountSnapshot(amount = 250.0, account = null, date = LocalDate.of(2020, 1, 1))
    }

    fun createAccounts(): List<Account> {
        return listOf(Account(id = 1L, name = "Account", currency = Currency.getInstance("CHF"), description = "Description", amount = 100.0),
                Account(id = 2L, name = "2ndAccount", currency = Currency.getInstance("EUR"), description = "2ndDescription", amount = 200.0))
    }

    fun createAccountSnapshots(): List<AccountSnapshot> {
        val account = createAccount()
        return listOf(
                AccountSnapshot(id = 1L, account = account, amount = 250.0, date = LocalDate.of(2020, 1, 1)),
                AccountSnapshot(id = 2L, account = account, amount = 350.0, date = LocalDate.of(2020, 3, 1))
        )
    }

    fun createPositions(): List<Position> {
        return listOf(
                Position(id = 1L, stock = createUsStock()),
                Position(id = 2L, stock = createChStock(),
                        transactions = listOf(createPosition()),
                        dividends = listOf(createDividend()))
        )
    }

    fun createUsStock(id: Long = 1L,
                      symbol: String = "ACN",
                      market: String = "US",
                      name: String = "Accenture",
                      currency: Currency = Currency.getInstance("USD")): Stock {
        return Stock(id = id, symbol = symbol, market = market, name = name, currency = currency, type = "EQS")
    }

    internal fun createChStock(id: Long = 2L,
                               symbol: String = "GEBN",
                               market: String = "SW",
                               name: String = "Geberit",
                               currency: Currency = Currency.getInstance("CHF")): Stock {
        return Stock(id = id, symbol = symbol, market = market, name = name, currency = currency, type = "EQS")
    }

    fun createCurrencyLookup(): CurrencyLookup {
        val currency = Currency.getInstance("USD")
        return CurrencyLookup(base = currency, date = LocalDate.now(), rates = mapOf(
                Currency.getInstance("CHF") to 1.1,
                Currency.getInstance("EUR") to 0.9
        ))
    }

    fun createExchange(): Exchange {
        return Exchange(listOf(
                Stock(name = "Accenture", symbol = "ACN", currency = usd(), type = "EQS", market = "US"),
                Stock(name = "Geberit", symbol = "GEBN", currency = chf(), type = "EQS", market = "SW"),
                Stock(name = "Swiss high dividends", symbol = "CHDVD", currency = chf(), type = "ETF", market = "SW")
        ))
    }

    private fun createDividend(): Dividend {
        return Dividend(id = 1L, totalAmount = 24.1, date = LocalDate.of(2020, 4, 18))
    }

    private fun createPosition(): Transaction {
        return Transaction(id = 1L, amount = 5, purchasePrice = 350.6, fees = 20.4, date = LocalDate.of(2020, 1, 15))
    }

    fun createDeposit(): Deposit {
        return createDeposits()[0]
    }

    fun createDeposits(): List<Deposit> {
        return listOf(
                Deposit(id = 1L, name = "Deposit", currency = Currency.getInstance("CHF"), description = "Description", amount = 100.0),
                Deposit(id = 2L, name = "2ndDeposit", currency = Currency.getInstance("EUR"), description = "2ndDescription", amount = 200.0)
        )
    }

    fun createDepositSnapshot(): DepositSnapshot {
        return createDepositSnapshots()[0]
    }

    fun createDepositSnapshotWithoutDeposit(): DepositSnapshot {
        return DepositSnapshot(amount = 250.0, deposit = null, date = LocalDate.of(2020, 1, 1))
    }

    fun createDepositSnapshots(): List<DepositSnapshot> {
        val deposit = createDeposit()
        return listOf(
                DepositSnapshot(id = 1L, deposit = deposit, amount = 250.0, date = LocalDate.of(2020, 1, 1)),
                DepositSnapshot(id = 2L, deposit = deposit, amount = 350.0, date = LocalDate.of(2020, 3, 1))
        )
    }
}