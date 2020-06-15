package com.mauquoi.money.business.service

import com.mauquoi.money.business.gateway.ecb.EcbGateway
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
}