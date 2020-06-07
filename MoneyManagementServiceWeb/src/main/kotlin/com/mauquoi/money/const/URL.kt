package com.mauquoi.money.const

import com.mauquoi.money.const.URL.PathVariable.ACCOUNT_ID
import com.mauquoi.money.const.URL.PathVariable.AUDIT_ID
import com.mauquoi.money.const.URL.PathVariable.USER_ID

object URL {

    object Account {
        const val BASE = "/api/v1/users/{$USER_ID}/accounts"
        const val ACCOUNT_BY_ID = "/{$ACCOUNT_ID}"
        const val UPDATE_ACCOUNT = "/{$ACCOUNT_ID}/update"
        const val ADD_ACCOUNT_AUDIT = "/{$ACCOUNT_ID}/audits"
        const val EDIT_ACCOUNT_AUDIT = "/{$ACCOUNT_ID}/audits/{$AUDIT_ID}"
    }

    object PathVariable {
        const val USER_ID = "userId"
        const val ACCOUNT_ID = "accountId"
        const val AUDIT_ID = "auditId"
    }
}