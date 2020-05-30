package com.mauquoi.money

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MoneyManagementApplication

fun main(args: Array<String>) {
	runApplication<MoneyManagementApplication>(*args)
}
