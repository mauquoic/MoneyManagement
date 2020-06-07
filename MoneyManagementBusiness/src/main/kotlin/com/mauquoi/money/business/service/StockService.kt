package com.mauquoi.money.business.service

import com.mauquoi.money.model.Stock
import com.mauquoi.money.repository.StockRepository
import com.mauquoi.money.repository.UserRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class StockService @Inject constructor(private val userRepository: UserRepository,
                                       private val stockRepository: StockRepository) {

    fun getStocks(userId: Long): List<Stock> {
        return stockRepository.findAllBelongingToUser(userId).toList()
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
//        val savedStock = stockRepository.findById(id)
//        val editedStock = savedStock.get().copy(name = stock.name,
//                currency = stock.currency,
//                amount = stock.amount,
//                description = stock.description)
        return stockRepository.save(stock)
    }

    fun getTotalStocksValue(userId: Long): Float {
        val stocks = stockRepository.findAllBelongingToUser(userId)
        return stocks.sumByDouble { it.calculateValue().toDouble() }.toFloat()
    }
}