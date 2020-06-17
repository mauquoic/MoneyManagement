package com.mauquoi.money.business.service

import com.mauquoi.money.business.gateway.finnhub.FinnhubGateway
import com.mauquoi.money.business.util.TestObjectCreator
import com.mauquoi.money.model.Dividend
import com.mauquoi.money.model.Position
import com.mauquoi.money.model.Stock
import com.mauquoi.money.repository.StockRepository
import com.mauquoi.money.repository.UserRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class StockServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var stockRepository: StockRepository

    @MockK
    private lateinit var finnhubGateway: FinnhubGateway

    private lateinit var stockService: StockService
    private val capturedUserId = slot<Long>()
    private val capturedStockId = slot<Long>()
    private val capturedDividendId = slot<Long>()
    private val capturedPositionId = slot<Long>()
    private val capturedStock = slot<Stock>()
    private val capturedPosition = slot<Position>()
    private val capturedDividend = slot<Dividend>()
    private val capturedShortForm = slot<String>()
    private val capturedExchange = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        stockService = StockService(userRepository, stockRepository, finnhubGateway)
    }

    @Test
    fun getStocks() {
        every { stockRepository.findByUserId(capture(capturedUserId)) } returns TestObjectCreator.createStocks().toSet()

        val stocks = stockService.getStocks(1L)

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(stocks[0].id, `is`(1L)) },
                { assertThat(stocks[1].id, `is`(2L)) }
        )
    }

    @Test
    fun getStock() {
        every { stockRepository.findById(capture(capturedStockId)) } returns Optional.of(TestObjectCreator.createStocks()[0])

        stockService.getStock(1L)

        assertThat(capturedStockId.captured, `is`(1L))
    }

    @Test
    fun addStock() {
        every { userRepository.findById(capture(capturedUserId)) } returns Optional.of(TestObjectCreator.createUser())
        every { stockRepository.save(capture(capturedStock)) } returns TestObjectCreator.createStocks()[0]

        val stockDto = TestObjectCreator.createStocks()[0]
        stockDto.id = null

        stockService.addStock(1L, stockDto)

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(capturedStock.captured.id, `is`(nullValue())) },
                { assertThat(capturedStock.captured.name, `is`("Accenture")) },
                { assertThat(capturedStock.captured.symbol, `is`("ACN")) },
                { assertThat(capturedStock.captured.market, `is`("US")) },
                { assertThat(capturedStock.captured.currency.currencyCode, `is`("USD")) }
        )
    }

    @Test
    fun editStock() {
        every { stockRepository.findById(capture(capturedStockId)) } returns Optional.of(TestObjectCreator.createStocks()[0])
        every { stockRepository.save(capture(capturedStock)) } returns TestObjectCreator.createStocks()[0]

        val stockDto = TestObjectCreator.createStocks()[1]

        stockService.editStock(1L, stockDto)

        assertAll(
                { assertThat(capturedStockId.captured, `is`(1L)) },
                { assertThat(capturedStock.captured.id, `is`(1L)) },
                { assertThat(capturedStock.captured.name, `is`("Geberit")) },
                { assertThat(capturedStock.captured.symbol, `is`("GEBN")) },
                { assertThat(capturedStock.captured.market, `is`("SW")) },
                { assertThat(capturedStock.captured.dividends.size, `is`(1)) },
                { assertThat(capturedStock.captured.positions.size, `is`(1)) },
                { assertThat(capturedStock.captured.currency.currencyCode, `is`("CHF")) }
        )
    }

    @Test
    fun getTotalStocksValue() {
        every { stockRepository.findByUserId(capture(capturedUserId)) } returns TestObjectCreator.createStocks().toSet()
        every { finnhubGateway.getStockPrice(any()) } returns TestObjectCreator.createQuoteDto()

        val value = stockService.getTotalStocksValue(1L)

        assertAll(
                { assertThat(value, `is`(9.5f)) },
                { assertThat(capturedUserId.captured, `is`(1L)) }
        )

    }

    @Test
    fun getStockPrice() {
        every { finnhubGateway.getStockPrice(capture(capturedShortForm)) } returns TestObjectCreator.createQuoteDto()
        stockService.getStockPrice("ACN")
        assertThat(capturedShortForm.captured, `is`("ACN"))
    }

    @Test
    fun getStockName_swissMarket_lookupDoneCorrectly() {

        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns TestObjectCreator.createExchangeDto()

        val stockName = stockService.getStockName("GEBN", "SW")

        assertAll(
                { assertThat(stockName.description, `is`("Geberit")) },
                { assertThat(capturedExchange.captured, `is`("SW")) }
        )
    }

    @Test
    fun getStockName_usMarket_lookupDoneCorrectly() {

        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns TestObjectCreator.createExchangeDto()

        val stockName = stockService.getStockName("ACN", "US")

        assertAll(
                { assertThat(stockName.description, `is`("Accenture")) },
                { assertThat(capturedExchange.captured, `is`("US")) }
        )
    }
}