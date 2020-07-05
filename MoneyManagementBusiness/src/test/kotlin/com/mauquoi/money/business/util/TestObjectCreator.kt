package com.mauquoi.money.business.util

import com.mauquoi.money.model.*
import com.mauquoi.money.model.audit.AccountSnapshot
import com.mauquoi.money.model.audit.DepositSnapshot
import com.mauquoi.money.model.dto.CurrencyLookupDto
import com.mauquoi.money.model.dto.ExchangeDto
import com.mauquoi.money.model.dto.FinnhubStockDto
import com.mauquoi.money.model.dto.QuoteDto
import java.time.Instant
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
                Position(id = 1L, stock = createUsStock(), transactions = listOf(createPosition())),
                Position(id = 2L, stock = createChStock(),
                        transactions = listOf(createPosition()),
                        dividends = listOf(createDividend()))
        )
    }

    fun createUsStock(): Stock {
        return Stock(id = 1L, symbol = "ACN", market = "US", name = "Accenture", currency = Currency.getInstance("USD"))
    }

    internal fun createChStock(): Stock {
        return Stock(id = 2L, symbol = "GEBN", market = "SW", name = "Geberit", currency = Currency.getInstance("CHF"))
    }

    fun createCurrencyLookupDto(): CurrencyLookupDto {
        val currency = Currency.getInstance("USD")
        return CurrencyLookupDto(base = currency, date = LocalDate.now(), rates = mapOf(
                Currency.getInstance("CHF") to 1.1,
                Currency.getInstance("EUR") to 0.9
        ))
    }

    fun createQuoteDto(): QuoteDto {
        return QuoteDto(open = 2.0, previousClose = 1.8, current = 1.9, low = 1.7, high = 2.1, timeStamp = Instant.now())
    }

    fun createExchangeDto(): ExchangeDto {
        return ExchangeDto(listOf(
                FinnhubStockDto(description = "Accenture", symbol = "ACN", displaySymbol = "ACN"),
                FinnhubStockDto(description = "Geberit", symbol = "GEBN.SW", displaySymbol = "GEBN.SW"),
                FinnhubStockDto(description = "Swiss high dividends", symbol = "CHDVD.SW", displaySymbol = "CHDVD.SW")
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
                Deposit(id = 1L, name = "2ndDeposit", currency = Currency.getInstance("EUR"), description = "2ndDescription", amount = 200.0)
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