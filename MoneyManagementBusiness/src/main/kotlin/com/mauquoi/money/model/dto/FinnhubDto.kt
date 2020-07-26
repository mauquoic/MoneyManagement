package com.mauquoi.money.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class QuoteDto(@set:JsonProperty("o") var open: Double,
                    @set:JsonProperty("h") var high: Double,
                    @set:JsonProperty("l") var low: Double,
                    @set:JsonProperty("c") var current: Double,
                    @set:JsonProperty("pc") var previousClose: Double,
                    @set:JsonProperty("t") var timeStamp: Instant)

data class FinnhubStockDto(val description: String,
                           val displaySymbol: String,
                           val symbol: String,
                           val currency: String? = null,
                           val type: String? = null)