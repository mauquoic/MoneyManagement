package com.mauquoi.money.business.service

import com.mauquoi.money.business.gateway.ecb.EcbGateway
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject

@Service
class CurrencyService @Inject constructor(private val ecbGateway: EcbGateway) {

    fun convertCurrency(amount: Float, from: Currency, to: Currency): Float {
        if (from == to) return 1f
        val conversionValues = ecbGateway.getConversionValues(from)
        return amount * (conversionValues.rates[to]?: throw RuntimeException("Could not convert to required currency"))
    }
}