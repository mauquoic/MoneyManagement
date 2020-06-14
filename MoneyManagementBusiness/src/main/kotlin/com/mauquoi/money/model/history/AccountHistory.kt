package com.mauquoi.money.model.history

import com.mauquoi.money.model.Account
import com.mauquoi.money.model.audit.AccountSnapshot

data class AccountHistory(val current: Account,
                          val history: List<AccountSnapshot>)