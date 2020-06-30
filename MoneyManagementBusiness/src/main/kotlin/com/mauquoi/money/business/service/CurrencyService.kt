package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.UnknownCurrencyException
import com.mauquoi.money.business.gateway.ecb.EcbGateway
import com.mauquoi.money.model.Account
import com.mauquoi.money.model.CurrencyItem
import com.mauquoi.money.model.OverviewItem
import com.mauquoi.money.model.ValueItem
import com.mauquoi.money.model.dto.CurrencyLookupDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
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

    fun createOverviewItem(stocks: List<ValueItem>, preferredCurrency: Currency): OverviewItem {
        val distribution = createDistributionMap(stocks)
        return OverviewItem(mainCurrency = preferredCurrency,
                mainCurrencyValue = calculateMainCurrencyValue(distribution, preferredCurrency),
                distribution = distribution)
    }

    private fun calculateMainCurrencyValue(distribution: Map<Currency, BigDecimal>, preferredCurrency: Currency): BigDecimal {
        return distribution.map { entry -> convertCurrency(preferredCurrency, entry.key, entry.value) }
                .fold(BigDecimal.ZERO) { acc, nextValue -> acc.plus(nextValue) }
                .setScale(2, RoundingMode.HALF_UP)
    }

    private fun createDistributionMap(stocks: List<ValueItem>): Map<Currency, BigDecimal> {
        return stocks.groupBy { it.currency() }.mapValues { entry -> entry.value.sumByDouble { it.value() }.toBigDecimal() }
    }

    private fun convertCurrency(base: Currency, to: Currency, amount: BigDecimal): BigDecimal {
        if (base == to) return amount
        val currencies = getRates(baseCurrency = base)
        return currencies.rates[to]?.let { amount.setScale(2).div(it.toBigDecimal()) } ?: throw UnknownCurrencyException(base)
    }
}
