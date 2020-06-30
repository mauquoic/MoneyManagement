package com.mauquoi.money.business.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

/**
 * Contains some errors class that can be thrown during runtime
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFoundException : RuntimeException("Nobody could be found by that ID.")

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class AccountNotFoundException : RuntimeException("No account could be found by that ID.")

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class DepositNotFoundException : RuntimeException("No deposit could be found by that ID.")

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class StockNotFoundException : RuntimeException("No stock could be found by that ID.")

@ResponseStatus(value = HttpStatus.PRECONDITION_REQUIRED)
class PreferredCurrencyUnknownException : RuntimeException("No currency preference known for the user.")

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
class UnknownCurrencyException(currency: Currency) : RuntimeException("Could not convert to the currency $currency.")

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UnknownMarketException(id: String) : RuntimeException("No market could be found by ID $id.")