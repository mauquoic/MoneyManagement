package com.mauquoi.money.business.gateway.finnhub

import com.mauquoi.money.business.util.TestObjectCreator
import com.mauquoi.money.business.util.TestObjectCreator.createStockDtos
import com.mauquoi.money.config.BusinessConfiguration
import com.mauquoi.money.model.dto.FinnhubStockDto
import com.mauquoi.money.model.dto.QuoteDto
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

internal class FinnhubGatewayTest {

    @MockK
    private lateinit var builder: RestTemplateBuilder

    @MockK
    private lateinit var restTemplate: RestTemplate

    private lateinit var finnhubGateway: FinnhubGateway

    private val capturedUrl = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        MockKAnnotations.init(this)
        every { builder.build() } returns restTemplate
        finnhubGateway = FinnhubGateway(builder, BusinessConfiguration().markets(), "baseUrl/v1", "token")
    }

    @Test
    fun getStockPrice() {
        every { restTemplate.getForEntity(capture(capturedUrl), QuoteDto::class.java) } returns ResponseEntity.ok(TestObjectCreator.createQuoteDto())

        finnhubGateway.getStockPrice("ACN")
        assertAll(
                { assertThat(capturedUrl.captured, Matchers.`is`("baseUrl/v1/quote?symbol=ACN&token=token")) }
        )
    }

    @Test
    fun getExchange() {
        every {
            restTemplate.exchange(capture(capturedUrl), HttpMethod.GET,
                    null, object : ParameterizedTypeReference<List<FinnhubStockDto>>() {})
        } returns ResponseEntity.ok(createStockDtos())

        val exchange = finnhubGateway.getExchange("US")
        assertAll(
                { assertThat(capturedUrl.captured, Matchers.`is`("baseUrl/v1/stock/symbol?exchange=US&token=token")) },
                { assertThat(exchange.stocks.size, Matchers.`is`(3)) }
        )
    }
}