package com.mauquoi.money.controller

import com.mauquoi.money.business.service.DepositService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(DepositController::class)
@ActiveProfiles("test")
internal class DepositControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var depositService: DepositService

    private val capturedUserId = slot<Long>()

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun getDeposits() {
    }

    @Test
    fun getDeposit() {
    }

    @Test
    fun putDeposit() {
    }

    @Test
    fun addDeposit() {
    }
}