package com.mauquoi.money.business.service

import com.mauquoi.money.business.error.StockNotFoundException
import com.mauquoi.money.business.gateway.finnhub.FinnhubGateway
import com.mauquoi.money.model.*
import com.mauquoi.money.model.dto.FinnhubStockDto
import com.mauquoi.money.repository.PositionRepository
import com.mauquoi.money.repository.StockRepository
import com.mauquoi.money.repository.UserRepository
import com.mauquoi.money.toNullable
import org.springframework.stereotype.Service
import java.util.*
import javax.inject.Inject

@Service
class StockService @Inject constructor(private val userRepository: UserRepository,
                                       private val stockRepository: StockRepository,
                                       private val positionRepository: PositionRepository,
                                       private val finnhubGateway: FinnhubGateway,
                                       private val markets: List<Market>) {

    fun getPositions(userId: Long): List<Position> {
        return positionRepository.findByUserId(userId).toList().sortedBy { it.id }
    }

    fun getStock(id: Long): Stock {
        return stockRepository.findById(id).orElseThrow { StockNotFoundException() }
    }

    fun addPosition(userId: Long, stockDto: Position): Position {
        val user = userRepository.findById(userId).get()
        val stock: Stock = findOrCreateStock(stockDto)
        val position = stockDto.copy(user = user, stock = stock)
        return positionRepository.save(position)
    }

    private fun findOrCreateStock(stockDto: Position): Stock {
        return stockRepository.findByLookup(stockDto.stock.createLookup()).toNullable()
                ?: stockRepository.save(stockDto.stock)
    }

    fun editPosition(id: Long, stock: Position) {
        val savedStock = getPosition(id)
        val editedStock = savedStock.copy(
                description = stock.description,
                transactions = editPositions(savedStock.transactions, stock.transactions),
                dividends = editDividends(savedStock.dividends, stock.dividends)
        )
        positionRepository.save(editedStock)
    }

    fun getPosition(id: Long): Position {
        return positionRepository.findById(id).orElseThrow { StockNotFoundException() }
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

    private fun editPositions(oldTransactions: List<Transaction>, newTransactions: List<Transaction>): List<Transaction> {
        return newTransactions.map { pos ->
            oldTransactions.find { it.id == pos.id }?.copy(amount = pos.amount,
                    purchasePrice = pos.purchasePrice,
                    fees = pos.fees,
                    date = pos.date) ?: pos
        }
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
                    symbol = if (exchange == "US") {
                        it.symbol
                    } else {
                        it.symbol.substringBeforeLast(".")
                    },
                    currency = markets.firstOrNull{m -> m.market == exchange}?.currency ?: Currency.getInstance("USD"))
        }
        stockRepository.saveAll(stocks.distinctBy { it.lookup })
    }

    fun getStockExchange(market: String): List<Stock> {
        return stockRepository.findAllByMarket(market).sortedBy { it.lookup }
    }

    fun addTransaction(positionId: Long, transaction: Transaction): Position {
        val position = getPosition(positionId)
        val transactions = position.transactions.toMutableList()
        transactions.add(transaction)
        return positionRepository.save(position.copy(transactions = transactions))
    }

    fun addDividend(positionId: Long, dividend: Dividend): Position {
        val position = getPosition(positionId)
        val dividends = position.dividends.toMutableList()
        dividends.add(dividend)
        return positionRepository.save(position.copy(dividends = dividends))
    }
}