package com.mauquoi.money.business.gateway.ecb

import com.mauquoi.money.business.gateway.Constants.Path.DATE
import com.mauquoi.money.business.gateway.Constants.Path.LATEST
import com.mauquoi.money.business.gateway.Constants.Query.BASE
import com.mauquoi.money.model.CurrencyLookup
import com.mauquoi.money.model.dto.CurrencyLookupDto
import com.mauquoi.money.mapping.fromDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@Component
class EcbGateway @Inject constructor(private val builder: RestTemplateBuilder,
                                     @Value("\${rest.ecb.url}") private val baseUrl: String) {

    @Cacheable(value = ["conversionValuesCache"])
    fun getConversionValues(baseCurrency: Currency, date: LocalDate? = null): CurrencyLookup {
        val restTemplate = builder.build()
        val pathVariables = mapOf(DATE to (date?.toString() ?: LATEST))
        val url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam(BASE, baseCurrency)
                .buildAndExpand(pathVariables)
                .toUriString()
        val exchangeRates = restTemplate.getForEntity(url, CurrencyLookupDto::class.java).body
        return exchangeRates?.fromDto() ?: throw RuntimeException("Could not retrieve the conversion values")
    }
}