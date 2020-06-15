package com.mauquoi.money.config

import com.mauquoi.money.rest.CustomRestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BusinessConfiguration {

    @Bean
    fun customRestTemplateCustomizer (): CustomRestTemplateCustomizer{
        return CustomRestTemplateCustomizer()
    }
}