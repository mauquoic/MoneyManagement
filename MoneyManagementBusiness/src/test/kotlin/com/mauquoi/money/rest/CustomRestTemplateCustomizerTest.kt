package com.mauquoi.money.rest

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.web.client.RestTemplate

internal class CustomRestTemplateCustomizerTest {

    private val restTemplate = RestTemplate()

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun customize_interceptorIsAdded() {
        CustomRestTemplateCustomizer().customize(restTemplate)

        assertAll(
                { assertThat(restTemplate.interceptors.size, `is`(1)) },
                { assertThat(restTemplate.interceptors[0] is CustomClientHttpRequestInterceptor, `is`(true)) }
        )
    }
}