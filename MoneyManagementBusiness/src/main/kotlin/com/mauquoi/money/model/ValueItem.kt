package com.mauquoi.money.model

interface ValueItem : CurrencyItem {

    fun value(): Double
}