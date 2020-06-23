package com.mauquoi.money.model.history

import com.mauquoi.money.model.Account
import com.mauquoi.money.model.Deposit
import com.mauquoi.money.model.audit.AccountSnapshot
import com.mauquoi.money.model.audit.DepositSnapshot

data class AccountHistory(val current: Account,
                          val history: List<AccountSnapshot>)

data class DepositHistory(val current: Deposit,
                          val history: List<DepositSnapshot>)