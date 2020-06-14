package com.mauquoi.money.business.gateway.ecb

import com.mauquoi.money.model.dto.CurrencyLookupDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import javax.inject.Inject

@Component
class EcbGateway @Inject constructor(private val builder: RestTemplateBuilder,
                                     @Value("\${rest.url.ecb}") private val baseUrl: String) {

    fun getConversionValues(baseCurrency: Currency): CurrencyLookupDto {
        val restTemplate = builder.build()
        val url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("base", baseCurrency)
                .buildAndExpand()
                .toUriString()
        val exchangeRates = restTemplate.getForEntity(url, CurrencyLookupDto::class.java).body
        return exchangeRates ?: throw RuntimeException("Could not retrieve the conversion values")
    }
}