package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.StockNotFoundException
import com.mauquoi.money.business.gateway.finnhub.FinnhubGateway
import com.mauquoi.money.business.util.TestObjectCreator
import com.mauquoi.money.business.util.TestObjectCreator.usd
import com.mauquoi.money.model.*
import com.mauquoi.money.repository.PositionRepository
import com.mauquoi.money.repository.StockRepository
import com.mauquoi.money.repository.UserRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
internal class StockServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var stockRepository: StockRepository

    @MockK
    private lateinit var positionRepository: PositionRepository

    @MockK
    private lateinit var finnhubGateway: FinnhubGateway

    private lateinit var stockService: StockService
    private val capturedUserId = slot<Long>()
    private val capturedStockId = slot<Long>()
    private val capturedDividend = slot<Dividend>()
    private val capturedStockList = slot<List<Stock>>()
    private val capturedPosition = slot<Position>()
    private val capturedStock = slot<Stock>()
    private val capturedLookup = slot<String>()
    private val capturedShortForm = slot<String>()
    private val capturedExchange = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        stockService = StockService(userRepository, stockRepository, positionRepository, finnhubGateway)
    }

    @Test
    fun getStocks() {
        every { positionRepository.findByUserId(capture(capturedUserId)) } returns TestObjectCreator.createPositions().toSet()

        val stocks = stockService.getPositions(1L)

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(stocks[0].id, `is`(1L)) },
                { assertThat(stocks[1].id, `is`(2L)) },
                { assertThat(stocks[0].transactions.size, `is`(1)) },
                { assertThat(stocks[1].transactions[0].id, `is`(1L)) },
                { assertThat(stocks[1].transactions[0].fees, `is`(20.4)) }
        )
    }

    @Test
    fun getStock() {
        every { positionRepository.findById(capture(capturedStockId)) } returns Optional.of(TestObjectCreator.createPositions()[0])

        stockService.getPosition(1L)

        assertThat(capturedStockId.captured, `is`(1L))
    }

    @Test
    fun addPosition_stockExists_noNewStockCreated() {
        every { userRepository.findById(capture(capturedUserId)) } returns Optional.of(TestObjectCreator.createUser())
        every { positionRepository.save(capture(capturedPosition)) } returns TestObjectCreator.createPositions()[0]
        every { stockRepository.findByLookup(capture(capturedLookup)) } returns Optional.of(TestObjectCreator.createUsStock())

        val stockDto = TestObjectCreator.createPositions()[0]
        stockDto.id = null

        stockService.addPosition(1L, stockDto)

        verify(exactly = 1) { stockRepository.findByLookup(any()) }
        verify(exactly = 0) { stockRepository.save(any<Stock>()) }

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(capturedPosition.captured.id, `is`(nullValue())) },
                { assertThat(capturedPosition.captured.stock.name, `is`("Accenture")) },
                { assertThat(capturedPosition.captured.stock.symbol, `is`("ACN")) },
                { assertThat(capturedPosition.captured.stock.market, `is`("US")) },
                { assertThat(capturedPosition.captured.currency().currencyCode, `is`("USD")) }
        )
    }

    @Test
    fun addPosition_stockDoesNotExist_newStockCreated() {
        every { userRepository.findById(capture(capturedUserId)) } returns Optional.of(TestObjectCreator.createUser())
        every { positionRepository.save(capture(capturedPosition)) } returns TestObjectCreator.createPositions()[0]
        every { stockRepository.save(capture(capturedStock)) } returns TestObjectCreator.createUsStock()
        every { stockRepository.findByLookup(capture(capturedLookup)) } returns Optional.empty()

        val stockDto = TestObjectCreator.createPositions()[0]
        stockDto.id = null

        stockService.addPosition(1L, stockDto)

        verify(exactly = 1) { stockRepository.findByLookup(any()) }
        verify(exactly = 1) { stockRepository.save(any<Stock>()) }

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(capturedPosition.captured.id, `is`(nullValue())) },
                { assertThat(capturedPosition.captured.stock.name, `is`("Accenture")) },
                { assertThat(capturedPosition.captured.stock.symbol, `is`("ACN")) },
                { assertThat(capturedPosition.captured.stock.market, `is`("US")) },
                { assertThat(capturedPosition.captured.currency().currencyCode, `is`("USD")) }
        )
    }

    @Test
    fun editStock() {
        every { positionRepository.findById(capture(capturedStockId)) } returns Optional.of(TestObjectCreator.createPositions()[1])
        every { positionRepository.save(capture(capturedPosition)) } returns TestObjectCreator.createPositions()[1]

        val stockDto = TestObjectCreator.createPositions()[1]

        stockService.editPosition(1L, stockDto)

        assertAll(
                { assertThat(capturedStockId.captured, `is`(1L)) },
                { assertThat(capturedPosition.captured.id, `is`(2L)) },
                { assertThat(capturedPosition.captured.stock.name, `is`("Geberit")) },
                { assertThat(capturedPosition.captured.stock.symbol, `is`("GEBN")) },
                { assertThat(capturedPosition.captured.stock.market, `is`("SW")) },
                { assertThat(capturedPosition.captured.dividends.size, `is`(1)) },
                { assertThat(capturedPosition.captured.transactions.size, `is`(1)) },
                { assertThat(capturedPosition.captured.currency().currencyCode, `is`("CHF")) }
        )
    }

    @Test
    fun editStock_withNewPosition_oldPositionsRemainUnchanged() {
        val oldStock = Position(id = 10L, stock = TestObjectCreator.createUsStock(),
                transactions = listOf(
                        Transaction(id = 1L, amount = 5, purchasePrice = 10.0, fees = 2.0, date = LocalDate.of(2020, 1, 15)),
                        Transaction(id = 2L, amount = 6, purchasePrice = 20.0, date = LocalDate.of(2020, 1, 19))
                ),
                dividends = listOf(
                        Dividend(id = 1L, totalAmount = 6.0, date = LocalDate.of(2020, 4, 18)),
                        Dividend(id = 2L, totalAmount = 7.0, date = LocalDate.of(2020, 6, 18))
                )
        )
        val newStock = Position(id = 10L, stock = TestObjectCreator.createUsStock(),
                transactions = listOf(
                        Transaction(id = 1L, amount = 5, purchasePrice = 10.0, fees = 2.0, date = LocalDate.of(2020, 1, 15)),
                        Transaction(amount = 10, purchasePrice = 30.0, fees = 10.0, date = LocalDate.of(2020, 5, 19))
                ),
                dividends = listOf(
                        Dividend(id = 1L, totalAmount = 8.0, date = LocalDate.of(2020, 4, 18)),
                        Dividend(totalAmount = 25.0, date = LocalDate.of(2020, 9, 18))
                )
        )
        every { positionRepository.findById(capture(capturedStockId)) } returns Optional.of(oldStock)
        every { positionRepository.save(capture(capturedPosition)) } returns newStock

        stockService.editPosition(10L, newStock)

        assertAll(
                { assertThat(capturedStockId.captured, `is`(10L)) },
                { assertThat(capturedPosition.captured.id, `is`(10L)) },
                { assertThat(capturedPosition.captured.transactions.size, `is`(2)) },
                { assertThat(capturedPosition.captured.transactions[0].id, `is`(1L)) },
                { assertThat(capturedPosition.captured.transactions[1].id, `is`(nullValue())) },
                { assertThat(capturedPosition.captured.transactions[0].amount, `is`(5)) },
                { assertThat(capturedPosition.captured.transactions[1].fees, `is`(10.0)) },
                { assertThat(capturedPosition.captured.dividends.size, `is`(2)) },
                { assertThat(capturedPosition.captured.dividends[0].id, `is`(1L)) },
                { assertThat(capturedPosition.captured.dividends[1].id, `is`(nullValue())) },
                { assertThat(capturedPosition.captured.dividends[0].totalAmount, `is`(8.0)) },
                { assertThat(capturedPosition.captured.dividends[1].date, `is`(LocalDate.of(2020, 9, 18))) }
        )
    }


    @Test
    fun getAccount_stockDoesNotExist_errorThrown() {
        every { stockRepository.findById(any()) } returns Optional.empty()

        val err = assertThrows<StockNotFoundException> { stockService.getStock(1L) }

        assertThat(err.localizedMessage, CoreMatchers.`is`("No stock could be found by that ID."))
    }

    @Test
    fun getStockPrice() {
        every { finnhubGateway.getStockPrice(capture(capturedShortForm)) } returns TestObjectCreator.createQuoteDto()
        stockService.getStockPrice("ACN")
        assertThat(capturedShortForm.captured, `is`("ACN"))
    }

    @Test
    fun getStockName_swissMarket_lookupDoneCorrectly() {

        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns TestObjectCreator.createExchange()

        val stockName = stockService.getStockName("GEBN", "SW")

        assertAll(
                { assertThat(stockName.name, `is`("Geberit")) },
                { assertThat(capturedExchange.captured, `is`("SW")) }
        )
    }

    @Test
    fun getStockName_usMarket_lookupDoneCorrectly() {

        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns TestObjectCreator.createExchange()

        val stockName = stockService.getStockName("ACN", "US")

        assertAll(
                { assertThat(stockName.name, `is`("Accenture")) },
                { assertThat(capturedExchange.captured, `is`("US")) }
        )
    }

    @Test
    fun getStock_stockExists() {
        every { stockRepository.findById(capture(capturedStockId)) } returns Optional.of(TestObjectCreator.createUsStock())
        val stock = stockService.getStock(1L)

        assertAll(
                { assertThat(stock.name, `is`("Accenture")) },
                { assertThat(capturedStockId.captured, `is`(1L)) }
        )
    }

    @Test
    fun getStock_stockDoesNotExist_errorIsThrown() {
        every { stockRepository.findById(capture(capturedStockId)) } returns Optional.empty()

        assertThrows<StockNotFoundException> { stockService.getStock(1L) }
    }

    @Test
    fun updateStockExchange_nonUS_lookupContainsMarket() {
        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns TestObjectCreator.createExchange()
        every { stockRepository.saveAll(capture(capturedStockList)) } returns emptyList()

        stockService.updateStockExchange("SW")

        assertAll(
                { assertThat(capturedStockList.captured.size, `is`(3)) },
                { assertThat(capturedStockList.captured[0].name, `is`("Accenture")) },
                { assertThat(capturedStockList.captured[2].lookup, `is`("CHDVD.SW")) }
        )
    }

    @Test
    fun updateStockExchange_duplicatesAreFilteredOut_usMarket_lookupDoesNotContainMarket() {
        val exchange = Exchange(listOf(
                Stock(name = "Accenture", symbol = "ACN", currency = usd(), type = "DS", market = "US"),
                Stock(name = "Accenture", symbol = "ACN", currency = usd(), type = "DEQ", market = "US"),
                Stock(name = "Agriculture Something", symbol = "AGM.A", currency = usd(), type = "ETF", market = "US")
        ))
        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns exchange
        every { stockRepository.saveAll(capture(capturedStockList)) } returns emptyList()

        stockService.updateStockExchange("US")

        assertAll(
                { assertThat(capturedStockList.captured.size, `is`(2)) },
                { assertThat(capturedStockList.captured[1].lookup, `is`("AGM.A")) }
        )
    }

    @Test
    fun addPosition() {
        every { positionRepository.findById(capture(capturedStockId)) } returns Optional.of(TestObjectCreator.createPositions()[0])
        every { positionRepository.save(capture(capturedPosition)) } returns TestObjectCreator.createPositions()[0]

        val position = Transaction(amount = 20, purchasePrice = 35.0, fees = 20.4, date = LocalDate.of(2020, 1, 20))
        stockService.addTransaction(1L, position)

        assertAll(
                { assertThat(capturedStockId.captured, `is`(1L)) },
                { assertThat(capturedPosition.captured.transactions.size, `is`(2)) },
                { assertThat(capturedPosition.captured.transactions[1].amount, `is`(20)) },
                { assertThat(capturedPosition.captured.transactions[1].purchasePrice, `is`(35.0)) },
                { assertThat(capturedPosition.captured.transactions[1].date.dayOfMonth, `is`(20)) }
        )
    }

    @Test
    fun addDividend() {
        every { positionRepository.findById(capture(capturedStockId)) } returns Optional.of(TestObjectCreator.createPositions()[0])
        every { positionRepository.save(capture(capturedPosition)) } returns TestObjectCreator.createPositions()[0]

        val dividend = Dividend(totalAmount = 24.2, date = LocalDate.of(2020, 4, 18))
        stockService.addDividend(3L, dividend)

        assertAll(
                { assertThat(capturedStockId.captured, `is`(3L)) },
                { assertThat(capturedPosition.captured.dividends.size, `is`(1)) },
                { assertThat(capturedPosition.captured.dividends[0].totalAmount, `is`(24.2)) },
                { assertThat(capturedPosition.captured.dividends[0].date.dayOfMonth, `is`(18)) }
        )
    }
}