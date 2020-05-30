package com.mauquoi.money.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.RuntimeException

@RestController
@RequestMapping("/api/overview")
class OverviewController {

    data class Overview(val cash: Int, val assets: Int, val deposits: Int, val cryptos: Int){
        val total: Int = cash + assets + deposits + cryptos
    }

    @GetMapping("/{userId}")
    fun getOverview(@PathVariable("userId") userId: Long): ResponseEntity<Overview> {
        return ResponseEntity.ok(Overview(300001, 95000, 7600, 26000))
    }

}