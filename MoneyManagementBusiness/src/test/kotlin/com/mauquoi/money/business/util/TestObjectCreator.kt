package com.mauquoi.money.business.util

import com.mauquoi.money.model.*
import com.mauquoi.money.model.audit.AccountSnapshot
import com.mauquoi.money.model.audit.DepositSnapshot
import com.mauquoi.money.model.dto.CurrencyLookupDto
import com.mauquoi.money.model.dto.FinnhubStockDto
import com.mauquoi.money.model.dto.QuoteDto
import java.time.Instant
import java.time.LocalDate
import java.util.*

object TestObjectCreator {

    fun usd(): Currency = Currency.getInstance("USD")
    fun chf(): Currency = Currency.getInstance("CHF")

    fun createUser(id: Long = 1L, email: String = "user@email.com", preferences: UserPreferences? = null): User {
        return User(id = id, email = email, preferences = preferences)
    }

    fun createUserWithPreferences(): User {
        return createUser(preferences = UserPreferences(1L, Locale.GERMAN, Currency.getInstance("USD")))
    }

    fun createAccount(id: Long = 1L,
                      name: String = "Account",
                      currency: Currency = Currency.getInstance("CHF"),
                      description: String = "Description",
                      amount: Double = 100.0
    ): Account {
        return Account(id = id, name = name, currency = currency, description = description, amount = amount)
    }

    fun createAccountSnapshot(): AccountSnapshot {
        return createAccountSnapshots()[0]
    }

    fun createAccountSnapshotWithoutAccount(amount: Double = 250.0,
                                            account: Account? = null,
                                            date: LocalDate = LocalDate.of(2020, 1, 1)): AccountSnapshot {
        return AccountSnapshot(amount = amount, account = account, date = date)
    }

    fun createAccounts(): List<Account> {
        return listOf(createAccount(),
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

    fun createUsStock(id: Long = 1L,
                      symbol: String = "ACN",
                      market: String = "US",
                      name: String = "Accenture",
                      currency: Currency = Currency.getInstance("USD")): Stock {
        return Stock(id = id, symbol = symbol, market = market, name = name, currency = currency, type = "EQS")
    }

    private fun createChStock(id: Long = 2L,
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

    fun createCurrencyLookupDto(): CurrencyLookupDto {
        val currency = Currency.getInstance("USD")
        return CurrencyLookupDto(base = currency, date = LocalDate.now(), rates = mapOf(
                Currency.getInstance("CHF") to 1.1,
                Currency.getInstance("EUR") to 0.9
        ))
    }

    fun createQuoteDto(open: Double = 2.0,
                       previousClose: Double = 1.8,
                       current: Double = 1.9,
                       low: Double = 1.7,
                       high: Double = 2.1,
                       timeStamp: Instant = Instant.now()
    ): QuoteDto {
        return QuoteDto(open = open, previousClose = previousClose, current = current, low = low, high = high, timeStamp = timeStamp)
    }

    fun createExchange(): Exchange {
        return Exchange(listOf(
                Stock(name = "Accenture", symbol = "ACN", currency = usd(), type = "EQS", market = "US"),
                Stock(name = "Geberit", symbol = "GEBN", currency = chf(), type = "EQS", market = "SW"),
                Stock(name = "Swiss high dividends", symbol = "CHDVD", currency = chf(), type = "ETF", market = "SW")
        ))
    }

    fun createStockDtos(): List<FinnhubStockDto> {
        return listOf(
                FinnhubStockDto(description = "Accenture", symbol = "ACN", displaySymbol = "ACN", currency = "USD", type = "DS"),
                FinnhubStockDto(description = "Geberit", symbol = "GEBN.SW", displaySymbol = "GEBN.SW", currency = "CHF", type = "DS"),
                FinnhubStockDto(description = "Swiss high dividends", symbol = "CHDVD.SW", displaySymbol = "CHDVD.SW", currency = "CHF", type = "DS")
        )
    }

    private fun createDividend(id: Long = 1L, totalAmount: Double = 24.1, date: LocalDate = LocalDate.of(2020, 4, 18)): Dividend {
        return Dividend(id = id, totalAmount = totalAmount, date = date)
    }

    private fun createPosition(id: Long = 1L,
                               amount: Int = 5,
                               date: LocalDate = LocalDate.of(2020, 4, 18),
                               purchasePrice: Double = 350.6,
                               fees: Double = 20.4
    ): Transaction {
        return Transaction(id = id, amount = amount, purchasePrice = purchasePrice, fees = fees, date = date)
    }

    fun createDeposit(id: Long = 1L,
                      name: String = "Deposit",
                      currency: Currency = Currency.getInstance("CHF"),
                      description: String = "Description",
                      amount: Double = 100.0): Deposit {
        return Deposit(id = id, name = name, currency = currency, description = description, amount = amount)
    }

    fun createDeposits(): List<Deposit> {
        return listOf(
                createDeposit(),
                Deposit(id = 1L, name = "2ndDeposit", currency = Currency.getInstance("EUR"), description = "2ndDescription", amount = 200.0)
        )
    }

    fun createDepositSnapshot(id: Long? = 1L,
                              deposit: Deposit? = createDeposit(),
                              amount: Double = 250.0,
                              date: LocalDate = LocalDate.of(2020, 1, 1)): DepositSnapshot {
        return DepositSnapshot(id = id, deposit = deposit, amount = amount, date = date)
    }

    fun createDepositSnapshots(): List<DepositSnapshot> {
        val deposit = createDeposit()
        return listOf(
                createDepositSnapshot(),
                DepositSnapshot(id = 2L, deposit = deposit, amount = 350.0, date = LocalDate.of(2020, 3, 1))
        )
    }
}