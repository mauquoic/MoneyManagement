package com.mauquoi.money.const

import com.mauquoi.money.const.URL.PathVariable.ACCOUNT_ID
import com.mauquoi.money.const.URL.PathVariable.AUDIT_ID
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

    object Currency {
        const val BASE = "$API_BASE/currencies"
        const val CONVERSION_RATES = "/conversion-rates"
    }

    object Shared {
        const val API_BASE = "/api/v1"
        const val USER_BASE = "$API_BASE/users"
    }

    object PathVariable {
        const val USER_ID = "userId"
        const val ACCOUNT_ID = "accountId"
        const val AUDIT_ID = "auditId"
    }

    object QueryParameter {
        const val BASE_CURRENCY = "base-currency"
        const val DATE = "date"
    }
}