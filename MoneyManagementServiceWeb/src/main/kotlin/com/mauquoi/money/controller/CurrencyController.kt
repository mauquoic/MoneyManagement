package com.mauquoi.money.controller

import com.mauquoi.money.business.service.CurrencyService
import com.mauquoi.money.const.URL
import com.mauquoi.money.const.URL.Currency.CONVERSION_RATES
import com.mauquoi.money.const.URL.QueryParameter.BASE_CURRENCY
import com.mauquoi.money.const.URL.QueryParameter.DATE
import com.mauquoi.money.model.dto.CurrencyLookupDto
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@RestController
@RequestMapping(URL.Currency.BASE)
class CurrencyController @Inject constructor(private val currencyService: CurrencyService) {

    @GetMapping
    fun getCurrencies(): ResponseEntity<List<Currency>> {
        return ResponseEntity.ok(currencyService.getCurrencies())
    }

    @GetMapping(CONVERSION_RATES, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getConversionRates(@RequestParam(value = BASE_CURRENCY) baseCurrency: Currency,
                           @RequestParam(value = DATE)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?) : ResponseEntity<CurrencyLookupDto> {
        return ResponseEntity.ok(currencyService.getRates(baseCurrency, date))
    }
}