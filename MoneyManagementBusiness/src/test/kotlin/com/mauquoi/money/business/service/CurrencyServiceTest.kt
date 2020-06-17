package com.mauquoi.money.business.service

import com.mauquoi.money.business.gateway.ecb.CurrencyConfiguration
import com.mauquoi.money.business.gateway.ecb.EcbGateway
import com.mauquoi.money.business.util.TestObjectCreator
import com.mauquoi.money.model.dto.CurrencyLookupDto
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
internal class CurrencyServiceTest {

    private lateinit var currencyService: CurrencyService
    @MockK
    lateinit var ecbGateway: EcbGateway

    private val capturedCurrency = slot<Currency>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        currencyService = CurrencyService(ecbGateway, CurrencyConfiguration().supportedCurrencies())
    }

    @Test
    fun getRates() {
        val currency = Currency.getInstance("USD")
        every { ecbGateway.getConversionValues(capture(capturedCurrency)) } returns TestObjectCreator.createCurrencyLookupDto()

        val rates = currencyService.getRates(currency)

        assertAll(
                { assertThat(capturedCurrency.captured.currencyCode, `is`("USD"))},
                { assertThat(rates.rates.size, `is`(2))}

        )
    }

    @Test
    fun getCurrencies() {
        val currencies = currencyService.getCurrencies()

        assertThat(currencies.size > 5, `is`(true))
    }
}