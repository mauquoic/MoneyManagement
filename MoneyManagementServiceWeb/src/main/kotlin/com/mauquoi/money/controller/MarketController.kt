package com.mauquoi.money.controller

import com.mauquoi.money.const.URL.Market.BASE
import com.mauquoi.money.model.Market
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
@RequestMapping(BASE)
class MarketController @Inject constructor(val markets: List<Market>) {

    @GetMapping
    fun getMarkets(): ResponseEntity<List<Market>> {
        return ResponseEntity.ok(markets)
    }

}