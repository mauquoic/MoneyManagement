package com.mauquoi.money.config

import com.mauquoi.money.model.Market
import com.mauquoi.money.rest.CustomRestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BusinessConfiguration {

    @Bean
    fun customRestTemplateCustomizer(): CustomRestTemplateCustomizer {
        return CustomRestTemplateCustomizer()
    }

    @Bean
    fun markets(): List<Market> {
        return listOf(
                Market("US", "United States Exchanges"),
                Market("SW", "Swiss Exchange"),
                Market("DE", "XETRA"),
                Market("SS", "Shanghai Stock Exchange"),
                Market("BR", "Euronext Brussels"),
                Market("SI", "Singapore Exchange")
        )
    }
}