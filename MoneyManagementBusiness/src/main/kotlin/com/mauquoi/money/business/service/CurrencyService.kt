package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.UnknownCurrencyException
import com.mauquoi.money.business.gateway.ecb.EcbGateway
import com.mauquoi.money.model.OverviewItem
import com.mauquoi.money.model.Stock
import com.mauquoi.money.model.dto.CurrencyLookupDto
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@Service
class CurrencyService @Inject constructor(private val ecbGateway: EcbGateway,
                                          private val supportedCurrencies: List<Currency>) {

    fun getRates(baseCurrency: Currency, date: LocalDate? = null): CurrencyLookupDto {
        return ecbGateway.getConversionValues(baseCurrency, date)
    }

    fun getCurrencies(): List<Currency> {
        return supportedCurrencies
    }

    fun createOverviewItem(stocks: List<Stock>, preferredCurrency: Currency): OverviewItem {
        val distribution = createDistributionMap(stocks)
        return OverviewItem(mainCurrency = preferredCurrency,
                mainCurrencyValue = calculateMainCurrencyValue(distribution, preferredCurrency),
                distribution = distribution)
    }

    private fun calculateMainCurrencyValue(distribution: Map<Currency, Float>, preferredCurrency: Currency): Float {
        return distribution.map { entry -> convertCurrency(preferredCurrency, entry.key, entry.value.toDouble()) }
                .sumByDouble { it }
                .toFloat()
    }

    private fun createDistributionMap(stocks: List<Stock>): Map<Currency, Float> {
        return stocks.groupBy { it.currency }.mapValues { entry -> entry.value.sumByDouble { it.calculateValue().toDouble() }.toFloat() }
    }

    private fun convertCurrency(base: Currency, to: Currency, amount: Double): Double {
        val currencies = getRates(baseCurrency = base)
        return currencies.rates[to]?.times(amount) ?: throw UnknownCurrencyException(base)
    }
}