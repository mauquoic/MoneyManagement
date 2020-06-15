package com.mauquoi.money.business.gateway.finnhub

import com.mauquoi.money.business.gateway.Constants
import com.mauquoi.money.business.gateway.Constants.Endpoint.QUOTE
import com.mauquoi.money.business.gateway.Constants.Query.EXCHANGE
import com.mauquoi.money.business.gateway.Constants.Query.SYMBOL
import com.mauquoi.money.business.gateway.Constants.Query.TOKEN
import com.mauquoi.money.model.dto.ExchangeDto
import com.mauquoi.money.model.dto.QuoteDto
import com.mauquoi.money.model.dto.StockDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import javax.inject.Inject


@Component
class FinnhubGateway @Inject constructor(private val builder: RestTemplateBuilder,
                                         @Value("\${rest.finnhub.url}") private val baseUrl: String,
                                         @Value("\${rest.finnhub.token}") private val token: String) {

    fun getStockPrice(shortForm: String): QuoteDto {
        val restTemplate = builder.build()
        val url = UriComponentsBuilder.fromUriString("$baseUrl$QUOTE")
                .queryParam(SYMBOL, shortForm)
                .queryParam(TOKEN, token)
                .build()
                .toUriString()
        val stockInfo = restTemplate.getForEntity(url, QuoteDto::class.java).body
        return stockInfo ?: throw RuntimeException("Could not retrieve the stock price")
    }

    fun getExchange(exchange: String): ExchangeDto {
        val restTemplate = builder.build()
        val url = UriComponentsBuilder.fromUriString("$baseUrl${Constants.Endpoint.SYMBOL}")
                .queryParam(EXCHANGE, exchange)
                .queryParam(TOKEN, token)
                .build()
                .toUriString()
        val stocks = restTemplate.exchange(url, HttpMethod.GET, null, object : ParameterizedTypeReference<List<StockDto>>() {}).body
                ?: throw RuntimeException("Could not retrieve the exchange information")
        return ExchangeDto(stocks)
    }

//    todo: Premium feature? News from company
}