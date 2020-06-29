package com.mauquoi.money.business.gateway.ecb

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class CurrencyConfiguration {

    @Bean
    fun supportedCurrencies(): List<Currency>{
        return listOf(
                Currency.getInstance("EUR"),
                Currency.getInstance("CAD"),
                Currency.getInstance("HKD"),
                Currency.getInstance("SGD"),
                Currency.getInstance("PHP"),
                Currency.getInstance("DKK"),
                Currency.getInstance("HUF"),
                Currency.getInstance("CZK"),
                Currency.getInstance("AUD"),
                Currency.getInstance("RON"),
                Currency.getInstance("SEK"),
                Currency.getInstance("IDR"),
                Currency.getInstance("BRL"),
                Currency.getInstance("RUB"),
                Currency.getInstance("HRK"),
                Currency.getInstance("JPY"),
                Currency.getInstance("THB"),
                Currency.getInstance("CHF"),
                Currency.getInstance("PLN"),
                Currency.getInstance("BGN"),
                Currency.getInstance("TRY"),
                Currency.getInstance("CNY"),
                Currency.getInstance("NOK"),
                Currency.getInstance("NZD"),
                Currency.getInstance("ZAR"),
                Currency.getInstance("USD"),
                Currency.getInstance("MXN"),
                Currency.getInstance("ILS"),
                Currency.getInstance("GBP"),
                Currency.getInstance("KRW"),
                Currency.getInstance("MYR")
        )
    }

    @Bean
    fun currenciesByMarkets(): Map<String, Currency>{
        return mapOf("US" to Currency.getInstance("USD"),
                "CN" to Currency.getInstance("CAD"),
                "VN" to Currency.getInstance("VND"),
                "SS" to Currency.getInstance("CNY"),
                "SG" to Currency.getInstance("EUR"),
                "BO" to Currency.getInstance("INR"),
                "HE" to Currency.getInstance("EUR"),
                "BC" to Currency.getInstance("COP"),
                "OL" to Currency.getInstance("NOK"),
                "ME" to Currency.getInstance("RUB"),
                "BR" to Currency.getInstance("EUR"),
                "NZ" to Currency.getInstance("NZD"),
                "SI" to Currency.getInstance("SGD"),
                "MX" to Currency.getInstance("MXN"),
                "T" to Currency.getInstance("JPY"),
                "JK" to Currency.getInstance("IDR"),
                "TO" to Currency.getInstance("CAD"),
                "AS" to Currency.getInstance("EUR"),
                "WA" to Currency.getInstance("PLN"),
                "BE" to Currency.getInstance("EUR"),
                "DU" to Currency.getInstance("EUR"),
                "AX" to Currency.getInstance("AUD"),
                "SA" to Currency.getInstance("BRL"),
                "JO" to Currency.getInstance("ZAR"),
                "IR" to Currency.getInstance("EUR"),
                "VI" to Currency.getInstance("EUR"),
                "MI" to Currency.getInstance("EUR"),
                "KS" to Currency.getInstance("KRW"),
                "ST" to Currency.getInstance("SEK"),
                "PA" to Currency.getInstance("EUR"),
                "F" to Currency.getInstance("EUR"),
                "DB" to Currency.getInstance("AED"),
                "PR" to Currency.getInstance("CZK"),
                "LS" to Currency.getInstance("EUR"),
                "RG" to Currency.getInstance("EUR"),
                "KQ" to Currency.getInstance("KRW"),
                "SW" to Currency.getInstance("CHF"),
                "KL" to Currency.getInstance("MYR"),
                "MU" to Currency.getInstance("EUR"),
                "VS" to Currency.getInstance("EUR"),
                "V" to Currency.getInstance("CAD"),
                "HK" to Currency.getInstance("HKD"),
                "SZ" to Currency.getInstance("CNY"),
                "TA" to Currency.getInstance("ILS"),
                "SR" to Currency.getInstance("SAR"),
                "NE" to Currency.getInstance("CAD"),
                "HM" to Currency.getInstance("EUR"),
                "BK" to Currency.getInstance("THB"),
                "NS" to Currency.getInstance("INR"),
                "TL" to Currency.getInstance("EUR"),
                "QA" to Currency.getInstance("QAR"),
                "IS" to Currency.getInstance("TRY"),
                "SN" to Currency.getInstance("CLP"),
                "TW" to Currency.getInstance("TWD"),
                "L" to Currency.getInstance("GBP"),
                "BA" to Currency.getInstance("ARS"),
                "MC" to Currency.getInstance("EUR"),
                "IC" to Currency.getInstance("ISK"),
                "AT" to Currency.getInstance("EUR"),
                "BD" to Currency.getInstance("HUF"),
                "CO" to Currency.getInstance("DKK"),
                "DE" to Currency.getInstance("EUR")
        )
    }
}