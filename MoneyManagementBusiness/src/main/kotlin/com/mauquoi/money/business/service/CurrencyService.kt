package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.UnknownCurrencyException
import com.mauquoi.money.business.gateway.ecb.EcbGateway
import com.mauquoi.money.model.Account
import com.mauquoi.money.model.OverviewItem
import com.mauquoi.money.model.StockPosition
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

    fun createOverviewItem(stocks: List<StockPosition>, preferredCurrency: Currency): OverviewItem {
        val distribution = createDistributionMap(stocks)
        return OverviewItem(mainCurrency = preferredCurrency,
                mainCurrencyValue = calculateMainCurrencyValue(distribution, preferredCurrency),
                distribution = distribution)
    }

    fun createAccountOverviewItem(accounts: List<Account>, preferredCurrency: Currency): OverviewItem {
        val distribution = createAccountDistributionMap(accounts)
        return OverviewItem(mainCurrency = preferredCurrency,
                mainCurrencyValue = calculateMainCurrencyValue(distribution, preferredCurrency),
                distribution = distribution)
    }

    private fun createAccountDistributionMap(accounts: List<Account>): Map<Currency, Double> {
        return accounts.groupBy { it.currency }.mapValues { entry -> entry.value.sumByDouble { it.amount } }
    }

    private fun calculateMainCurrencyValue(distribution: Map<Currency, Double>, preferredCurrency: Currency): Double {
        return distribution.map { entry -> convertCurrency(preferredCurrency, entry.key, entry.value) }
                .sumByDouble { it }
    }

    private fun createDistributionMap(stocks: List<StockPosition>): Map<Currency, Double> {
        return stocks.groupBy { it.currency() }.mapValues { entry -> entry.value.sumByDouble { it.calculateValue() } }
    }

    private fun convertCurrency(base: Currency, to: Currency, amount: Double): Double {
        val currencies = getRates(baseCurrency = base)
        return currencies.rates[to]?.let { amount.div(it) } ?: throw UnknownCurrencyException(base)
    }
}
