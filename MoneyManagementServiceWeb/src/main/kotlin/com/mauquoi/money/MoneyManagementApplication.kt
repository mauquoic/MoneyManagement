package com.mauquoi.money

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class MoneyManagementApplication

fun main(args: Array<String>) {
	runApplication<MoneyManagementApplication>(*args)
}
