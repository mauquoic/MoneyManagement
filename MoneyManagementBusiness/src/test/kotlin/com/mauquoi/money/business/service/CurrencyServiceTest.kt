package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.MarketCurrencyMismatchException
import com.mauquoi.money.business.error.MarketNotFoundException
import com.mauquoi.money.business.gateway.ecb.CurrencyConfiguration
import com.mauquoi.money.business.gateway.ecb.EcbGateway
import com.mauquoi.money.business.util.TestObjectCreator
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
internal class CurrencyServiceTest {

    private lateinit var currencyService: CurrencyService
    @MockK
    lateinit var ecbGateway: EcbGateway

    private val capturedCurrency = slot<Currency>()
    val usd = Currency.getInstance("USD")

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        currencyService = CurrencyService(ecbGateway, CurrencyConfiguration().supportedCurrencies(), CurrencyConfiguration().currenciesByMarkets())
    }

    @Test
    fun getRates() {
        every { ecbGateway.getConversionValues(capture(capturedCurrency)) } returns TestObjectCreator.createCurrencyLookupDto()

        val rates = currencyService.getRates(usd)

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

    @Test
    fun createOverviewItem() {

        every { ecbGateway.getConversionValues(capture(capturedCurrency)) } returns TestObjectCreator.createCurrencyLookupDto()
        val stocks = TestObjectCreator.createStockPositions()

        val overviewItem = currencyService.createOverviewItem(stocks, usd)

        assertAll(
                { assertThat(overviewItem.distribution.size, `is`(2))},
                { assertThat(overviewItem.distribution[usd], `is`(BigDecimal.valueOf(25.0)))},
                { assertThat(overviewItem.distribution[Currency.getInstance("CHF")], `is`(BigDecimal.valueOf(25.0)))},
                { assertThat(overviewItem.mainCurrency, `is`(usd))},
                { assertThat(overviewItem.mainCurrencyValue, `is`(BigDecimal.valueOf(47.73)))}
        )
    }

    @Test
    fun getCurrencyForMarket_marketFound_currencyReturned(){
        assertThat(currencyService.getCurrencyForMarket("US"), `is`(usd))
    }

    @Test
    fun getCurrencyForMarket_marketNotFound_errorThrown(){
        val err = assertThrows<MarketNotFoundException> { currencyService.getCurrencyForMarket("USS") }

        assertThat(err.localizedMessage, CoreMatchers.`is`("No market could be found by ID USS."))
    }

    @Test
    fun verifyCurrencyCompatibility_marketNotFound_errorThrown(){
        val err = assertThrows<MarketNotFoundException> { currencyService.verifyCurrencyCompatibility("USS", usd) }

        assertThat(err.localizedMessage, CoreMatchers.`is`("No market could be found by ID USS."))
    }

    @Test
    fun verifyCurrencyCompatibility_mismatch_errorThrown(){
        val err = assertThrows<MarketCurrencyMismatchException> { currencyService.verifyCurrencyCompatibility("SW", usd) }

        assertThat(err.localizedMessage, CoreMatchers.`is`("The market SW is not traded in USD."))
    }

    @Test
    fun verifyCurrencyCompatibility_match_noErrorThrown(){
        currencyService.verifyCurrencyCompatibility("US", usd)
    }
}