package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.StockNotFoundException
import com.mauquoi.money.business.gateway.finnhub.FinnhubGateway
import com.mauquoi.money.model.Dividend
import com.mauquoi.money.model.Position
import com.mauquoi.money.model.Stock
import com.mauquoi.money.model.dto.FinnhubStockDto
import com.mauquoi.money.repository.StockRepository
import com.mauquoi.money.repository.UserRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class StockService @Inject constructor(private val userRepository: UserRepository,
                                       private val stockRepository: StockRepository,
                                       private val finnhubGateway: FinnhubGateway) {

    fun getStocks(userId: Long): List<Stock> {
        return stockRepository.findByUserId(userId).toList().sortedBy { it.id }
    }

    fun getStock(id: Long): Stock {
        return stockRepository.findById(id).orElseThrow{ StockNotFoundException() }
    }

    fun addStock(userId: Long, stockDto: Stock): Stock {
        val user = userRepository.findById(userId).get()
        val stock = stockDto.copy(user = user)
        return stockRepository.save(stock)
    }

    fun editStock(id: Long, stock: Stock): Stock {
        val savedStock = getStock(id)
        val editedStock = savedStock.copy(name = stock.name,
                symbol = stock.symbol,
                currency = stock.currency,
                description = stock.description,
                market = stock.market,
                positions = editPositions(savedStock.positions, stock.positions),
                dividends = editDividends(savedStock.dividends, stock.dividends)
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

    fun getTotalStocksValue(userId: Long): Float {
        val stocks = getStocks(userId)
        stocks.forEach { it.value = finnhubGateway.getStockPrice(it.createSymbol()).current }
        return stocks.sumByDouble { it.calculateValue().toDouble() }.toFloat()
    }

    fun getStockPrice(symbol: String): Float {
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
}