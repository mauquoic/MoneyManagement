package com.mauquoi.money.controller

import com.mauquoi.money.business.service.*
import com.mauquoi.money.model.OverviewItem
import com.mauquoi.money.model.UserPreferences
import com.mauquoi.money.util.TestObjectCreator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.util.*

@WebMvcTest(OverviewController::class)
@ActiveProfiles("test")
internal class OverviewControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    @MockkBean
    private lateinit var accountService: AccountService

    @MockkBean
    private lateinit var depositService: DepositService

    @MockkBean
    private lateinit var stockService: StockService

    @MockkBean
    private lateinit var currencyService: CurrencyService

    private val capturedUserId = slot<Long>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun getOverview() {
        every { stockService.getStockPositions(any()) } returns TestObjectCreator.createStockPositions()
        every { accountService.getAccounts(any()) } returns TestObjectCreator.createAccounts()
        val user = TestObjectCreator.createUser()
        every { userService.getUser(any()) } returns user.copy(preferences = UserPreferences())
        every { currencyService.createOverviewItem(any(), any()) } returns OverviewItem(mainCurrencyValue = BigDecimal.ONE,
                mainCurrency = Currency.getInstance("USD"),
                distribution = mapOf(Currency.getInstance("USD") to BigDecimal.ONE,
                        Currency.getInstance("CHF") to BigDecimal.ZERO))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1/overview")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("stocks.mainCurrency", CoreMatchers.`is`("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks.mainCurrencyValue", CoreMatchers.`is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks.distribution.USD", CoreMatchers.`is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks.distribution.CHF", CoreMatchers.`is`(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("accounts.distribution.USD", CoreMatchers.`is`(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("accounts.mainCurrencyValue", CoreMatchers.`is`(1)))

    }
}