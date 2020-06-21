package com.mauquoi.money.controller

import com.mauquoi.money.business.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(OverviewController::class)
@ActiveProfiles("test")
internal class OverviewControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    private val capturedUserId = slot<Long>()

//    @BeforeEach
//    fun setUp() {
//    }
//
//    @Test
//    fun getOverview() {
//    }
}