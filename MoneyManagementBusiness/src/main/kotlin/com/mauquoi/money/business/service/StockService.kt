package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.StockNotFoundException
import com.mauquoi.money.business.gateway.finnhub.FinnhubGateway
import com.mauquoi.money.model.Dividend
import com.mauquoi.money.model.Position
import com.mauquoi.money.model.Stock
import com.mauquoi.money.model.StockPosition
import com.mauquoi.money.model.dto.FinnhubStockDto
import com.mauquoi.money.repository.StockPositionRepository
import com.mauquoi.money.repository.StockRepository
import com.mauquoi.money.repository.UserRepository
import com.mauquoi.money.toNullable
import org.springframework.stereotype.Service
import java.util.*
import javax.inject.Inject

@Service
class StockService @Inject constructor(private val userRepository: UserRepository,
                                       private val stockRepository: StockRepository,
                                       private val stockPositionRepository: StockPositionRepository,
                                       private val finnhubGateway: FinnhubGateway,
                                       private val currenciesByMarkets: Map<String, Currency>) {

    fun getStockPositions(userId: Long): List<StockPosition> {
        return stockPositionRepository.findByUserId(userId).toList().sortedBy { it.id }
    }

    fun getStock(id: Long): Stock {
        return stockRepository.findById(id).orElseThrow { StockNotFoundException() }
    }

    fun addStockPosition(userId: Long, stockDto: StockPosition): StockPosition {
        val user = userRepository.findById(userId).get()
        val stock: Stock = findOrCreateStock(stockDto)
        val stockPosition = stockDto.copy(user = user, stock = stock)
        return stockPositionRepository.save(stockPosition)
    }

    private fun findOrCreateStock(stockDto: StockPosition): Stock {
        return stockRepository.findByLookup(stockDto.stock.createLookup()).toNullable()
                ?: stockRepository.save(stockDto.stock)
    }

    fun editStockPosition(id: Long, stock: StockPosition) {
        val savedStock = getStockPosition(id)
        val editedStock = savedStock.copy(
                description = stock.description,
                positions = editPositions(savedStock.positions, stock.positions),
                dividends = editDividends(savedStock.dividends, stock.dividends)
        )
        stockPositionRepository.save(editedStock)
    }

    fun getStockPosition(id: Long): StockPosition {
        return stockPositionRepository.findById(id).orElseThrow { StockNotFoundException() }
    }

    fun editStock(id: Long, stock: Stock): Stock {
        val savedStock = getStock(id)
        val editedStock = savedStock.copy(name = stock.name,
                symbol = stock.symbol,
                currency = stock.currency,
                market = stock.market
        )
        return stockRepository.save(editedStock)
    }

    private fun editDividends(oldDividends: List<Dividend>, newDividends: List<Dividend>): List<Dividend> {
        return newDividends.map { pos ->
            oldDividends.find { it.id == pos.id }?.copy(totalAmount = pos.totalAmount,
                    date = pos.date) ?: pos
        }
    }

    private fun editPositions(oldPositions: List<Position>, newPositions: List<Position>): List<Position> {
        return newPositions.map { pos ->
            oldPositions.find { it.id == pos.id }?.copy(amount = pos.amount,
                    purchasePrice = pos.purchasePrice,
                    fees = pos.fees,
                    date = pos.date) ?: pos
        }
    }

    fun getTotalStocksValue(userId: Long): Double {
        val stocks = getStockPositions(userId)
        stocks.forEach { it.stock.value = finnhubGateway.getStockPrice(it.stock.createLookup()).current }
        return stocks.sumByDouble { it.calculateValue() }
    }

    fun getStockPrice(symbol: String): Double {
        return finnhubGateway.getStockPrice(symbol).current
    }

    fun getStockName(symbol: String, exchange: String): FinnhubStockDto {
        val exchangeDto = finnhubGateway.getExchange(exchange)
        return if (exchange != "US") {
            exchangeDto.stocks.first { it.symbol == "$symbol.$exchange" }
        } else {
            exchangeDto.stocks.first { it.symbol == symbol }
        }
    }

    fun updateStockExchange(exchange: String) {
        val exchangeDto = finnhubGateway.getExchange(exchange)
        val stocks = exchangeDto.stocks.map {
            Stock(name = it.description,
                    market = exchange,
                    symbol = it.symbol.substringBefore("."),
                    currency = currenciesByMarkets[exchange]?: Currency.getInstance("USD"))
        }
        stockRepository.saveAll(stocks)
    }
}