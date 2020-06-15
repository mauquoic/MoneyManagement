package com.mauquoi.money.business.service

import com.mauquoi.money.business.gateway.finnhub.FinnhubGateway
import com.mauquoi.money.model.Dividend
import com.mauquoi.money.model.Position
import com.mauquoi.money.model.Stock
import com.mauquoi.money.model.dto.QuoteDto
import com.mauquoi.money.model.dto.StockDto
import com.mauquoi.money.repository.StockRepository
import com.mauquoi.money.repository.UserRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class StockService @Inject constructor(private val userRepository: UserRepository,
                                       private val stockRepository: StockRepository,
                                       private val finnhubGateway: FinnhubGateway) {

    fun getStocks(userId: Long): List<Stock> {
        return stockRepository.findAllBelongingToUser(userId).toList().sortedBy { it.id }
    }

    fun getStock(id: Long): Stock {
        return stockRepository.findById(id).get()
    }

    fun addStock(userId: Long, stockDto: Stock): Stock {
        val user = userRepository.findById(userId).get()
        val stock = stockDto.copy(user = user)
        return stockRepository.save(stock)
    }

    fun editStock(id: Long, stock: Stock): Stock {
        val savedStock = stockRepository.findById(id).get()
        val editedStock = savedStock.copy(name = stock.name,
                symbol = stock.symbol,
                currency = stock.currency,
                description = stock.description,
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
        return stocks.sumByDouble { it.calculateValue().toDouble() }.toFloat()
    }

    fun getStockPrice(symbol: String): QuoteDto {
        return finnhubGateway.getStockPrice(symbol)
    }

    fun getStockName(symbol: String, exchange: String): StockDto {
        val exchangeDto = finnhubGateway.getExchange(exchange)
        if (exchange != "US") {
            return exchangeDto.stocks.first { it.symbol == "$symbol.$exchange" }
        } else {
            return exchangeDto.stocks.first { it.symbol == symbol }
        }
    }
}