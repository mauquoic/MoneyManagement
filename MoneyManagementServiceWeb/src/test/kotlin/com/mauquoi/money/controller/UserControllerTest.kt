package com.mauquoi.money.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mauquoi.money.business.service.UserService
import com.mauquoi.money.model.UserPreferences
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(UserController::class)
@ActiveProfiles("test")
internal class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    private val capturedUserId = slot<Long>()
    private val capturedPreferences = slot<UserPreferences>()
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun getPreferences() {
        every { userService.getPreferences(capture(capturedUserId)) } returns UserPreferences()

        mockMvc.perform(get("/api/v1/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("locale", `is`("en_GB")))

        assertThat(capturedUserId.captured, `is`(1L))
    }

    @Test
    fun updatePreferences() {
        every { userService.updatePreferences(capture(capturedUserId), capture(capturedPreferences)) } just runs

        val preferences = UserPreferences(currency = Currency.getInstance("CNY"))

        mockMvc.perform(put("/api/v1/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences)))
                .andExpect(status().isNoContent)

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(capturedPreferences.captured.currency.currencyCode, `is`("CNY")) }
        )
    }
}