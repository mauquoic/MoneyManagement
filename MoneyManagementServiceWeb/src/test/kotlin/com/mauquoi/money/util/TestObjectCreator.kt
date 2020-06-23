package com.mauquoi.money.util

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

    fun createStocks(): List<Stock> {
        return listOf(
                Stock(id = 1L, name = "Accenture", symbol = "ACN", market = "US", currency = Currency.getInstance("USD")),
                Stock(id = 2L, name = "Geberit", symbol = "GEBN", market = "SW", currency = Currency.getInstance("CHF"),
                        positions = listOf(createPosition()),
                        dividends = listOf(createDividend()))
        )
    }

    fun createCurrencyLookupDto(): CurrencyLookupDto {
        val currency = Currency.getInstance("USD")
        return CurrencyLookupDto(base = currency, date = LocalDate.now(), rates = mapOf(
                Currency.getInstance("CHF") to 1.1f,
                Currency.getInstance("EUR") to 0.9f
        ))
    }

    fun createQuoteDto(): QuoteDto {
        return QuoteDto(open = 2F, previousClose = 1.8f, current = 1.9f, low = 1.7f, high = 2.1f, timeStamp = Instant.now())
    }

    fun createExchangeDto(): ExchangeDto {
        return ExchangeDto(listOf(
                FinnhubStockDto(description = "Accenture", symbol = "ACN", displaySymbol = "ACN"),
                FinnhubStockDto(description = "Geberit", symbol = "GEBN.SW", displaySymbol = "GEBN.SW"),
                FinnhubStockDto(description = "Swiss high dividends", symbol = "CHDVD.SW", displaySymbol = "CHDVD.SW")
        ))
    }

    private fun createDividend(): Dividend {
        return Dividend(id = 1L, totalAmount = 24.1f, date = LocalDate.of(2020, 4, 18))
    }

    private fun createPosition(): Position {
        return Position(id = 1L, amount = 5, purchasePrice = 350.6f, fees = 20.4f, date = LocalDate.of(2020, 1, 15))
    }

    fun createDeposit(): Deposit {
        return createDeposits()[0]
    }

    fun createDeposits(): List<Deposit> {
        return listOf(
                Deposit(id = 1L, name = "Deposit", currency = Currency.getInstance("CHF"), description = "Description", amount = 100F),
                Deposit(id = 2L, name = "2ndDeposit", currency = Currency.getInstance("EUR"), description = "2ndDescription", amount = 200F)
        )
    }

    fun createDepositSnapshot(): DepositSnapshot {
        return createDepositSnapshots()[0]
    }

    fun createDepositSnapshotWithoutDeposit(): DepositSnapshot {
        return DepositSnapshot(amount = 250f, deposit = null, date = LocalDate.of(2020, 1, 1))
    }

    fun createDepositSnapshots(): List<DepositSnapshot> {
        val deposit = createDeposit()
        return listOf(
                DepositSnapshot(id = 1L, deposit = deposit, amount = 250f, date = LocalDate.of(2020, 1, 1)),
                DepositSnapshot(id = 2L, deposit = deposit, amount = 350f, date = LocalDate.of(2020, 3, 1))
        )
    }
}