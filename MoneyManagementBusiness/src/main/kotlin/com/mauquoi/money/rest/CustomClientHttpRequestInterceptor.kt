package com.mauquoi.money.rest

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse


class CustomClientHttpRequestInterceptor : ClientHttpRequestInterceptor {

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        logRequestDetails(request)
        return execution.execute(request, body)
    }

    private fun logRequestDetails(request: HttpRequest) {
        LOGGER.info("Headers: {}", request.headers)
        LOGGER.info("Request Method: {}", request.method)
        LOGGER.info("Request URI: {}", request.uri)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CustomClientHttpRequestInterceptor::class.java)
    }
}