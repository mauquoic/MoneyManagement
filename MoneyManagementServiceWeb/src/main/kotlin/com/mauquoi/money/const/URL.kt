package com.mauquoi.money.const

import com.mauquoi.money.const.URL.PathVariable.ACCOUNT_ID
import com.mauquoi.money.const.URL.PathVariable.AUDIT_ID
import com.mauquoi.money.const.URL.PathVariable.DEPOSIT_ID
import com.mauquoi.money.const.URL.PathVariable.MARKET
import com.mauquoi.money.const.URL.PathVariable.POSITION_ID
import com.mauquoi.money.const.URL.PathVariable.STOCK_SYMBOL
import com.mauquoi.money.const.URL.PathVariable.USER_ID
import com.mauquoi.money.const.URL.Shared.API_BASE
import com.mauquoi.money.const.URL.Shared.USER_BASE

object URL {

    object Account {
        const val BASE = "$USER_BASE/{$USER_ID}/accounts"
        const val ACCOUNT_BY_ID = "/{$ACCOUNT_ID}"
        const val UPDATE_ACCOUNT = "/{$ACCOUNT_ID}/update"
        const val GET_ACCOUNT_HISTORY = "/{$ACCOUNT_ID}/history"
        const val ADD_ACCOUNT_AUDIT = "/{$ACCOUNT_ID}/audits"
        const val EDIT_ACCOUNT_AUDIT = "/{$ACCOUNT_ID}/audits/{$AUDIT_ID}"
    }

    object Deposit {
        const val BASE = "$USER_BASE/{$USER_ID}/deposits"
        const val DEPOSIT_BY_ID = "/{$DEPOSIT_ID}"
        const val UPDATE_DEPOSIT = "/{$DEPOSIT_ID}/update"
        const val GET_DEPOSIT_HISTORY = "/{$DEPOSIT_ID}/history"
        const val ADD_DEPOSIT_AUDIT = "/{$DEPOSIT_ID}/audits"
        const val EDIT_DEPOSIT_AUDIT = "/{$DEPOSIT_ID}/audits/{$AUDIT_ID}"
    }

    object Currency {
        const val BASE = "$API_BASE/currencies"
        const val CONVERSION_RATES = "/conversion-rates"
    }

    object Stock {
        const val STOCK_QUOTE = "/stocks/{$STOCK_SYMBOL}"
        const val STOCK_NAME = "/markets/{$MARKET}/stocks/{$STOCK_SYMBOL}"
        const val STOCKS_BY_MARKET = "/markets/{$MARKET}/stocks"
    }

    object Position {
        const val BASE = "$USER_BASE/{$USER_ID}/positions"
        const val POSITIONS_BY_ID = "/{$POSITION_ID}"
        const val TRANSACTION = "/{$POSITION_ID}/transactions"
        const val DIVIDEND = "/{$POSITION_ID}/dividends"
    }

    object Shared {
        const val API_BASE = "/api/v1"
        const val USERS = "/users"
        const val USER_BASE = "$API_BASE$USERS"
    }

    object PathVariable {
        const val USER_ID = "userId"
        const val POSITION_ID = "positionId"
        const val ACCOUNT_ID = "accountId"
        const val DEPOSIT_ID = "depositId"
        const val AUDIT_ID = "auditId"
        const val STOCK_SYMBOL = "symbol"
        const val MARKET = "market"
    }

    object QueryParameter {
        const val BASE_CURRENCY = "base-currency"
        const val DATE = "date"
    }
}