package com.mauquoi.money.rest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import org.springframework.mock.http.client.MockClientHttpResponse
import java.net.URI


internal class CustomClientHttpRequestInterceptorTest {

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun intercept() {
        val request = Request()
        CustomClientHttpRequestInterceptor().intercept(request, ByteArray(0), RequestExecution())
    }

    private class Request : HttpRequest {

        private val headers = HttpHeaders()

        override fun getHeaders(): HttpHeaders {
            return headers
        }

        override fun getMethodValue(): String {
            return ""
        }

        override fun getURI(): URI {
            return URI("")
        }
    }

    private class RequestExecution : ClientHttpRequestExecution {

        override fun execute(p0: HttpRequest, p1: ByteArray): ClientHttpResponse {
            return MockClientHttpResponse(ByteArray(0), HttpStatus.ACCEPTED)
        }
    }
}