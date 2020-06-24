package com.mauquoi.money.model

import java.util.*

interface ValueItem {

    fun currency(): Currency

    fun value(): Double
}