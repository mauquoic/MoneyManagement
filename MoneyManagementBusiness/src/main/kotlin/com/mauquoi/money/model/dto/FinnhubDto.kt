package com.mauquoi.money.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class QuoteDto(@set:JsonProperty("o") var open: Float,
                    @set:JsonProperty("h") var high: Float,
                    @set:JsonProperty("l") var low: Float,
                    @set:JsonProperty("c") var current: Float,
                    @set:JsonProperty("pc") var previousClose: Float,
                    @set:JsonProperty("t") var timeStamp: Instant)

data class ExchangeDto(val stocks: List<StockDto>)

data class StockDto(val description: String,
                    val displaySymbol: String,
                    val symbol: String)