package com.mauquoi.money

import java.util.*

fun <T> Optional<T>.toNullable(): T? = this.orElse(null)