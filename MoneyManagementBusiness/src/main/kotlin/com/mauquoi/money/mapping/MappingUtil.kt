package com.mauquoi.money.mapping

class MappingUtil {

    companion object {
        fun getSymbol(market: String, symbol: String): String {
            return if (market == "US") {
                symbol
            } else {
                symbol.substringBeforeLast(".")
            }
        }
    }
}