package com.mauquoi.money.business.gateway

object Constants {

    object Endpoint {
        const val QUOTE = "/quote"
        const val SYMBOL = "/stock/symbol"
    }

    object Path {
        const val DATE = "date"
        const val LATEST = "latest"
    }

    object Query {
        const val BASE = "base"
        const val SYMBOL = "symbol"
        const val TOKEN = "token"
        const val EXCHANGE = "exchange"
    }
}