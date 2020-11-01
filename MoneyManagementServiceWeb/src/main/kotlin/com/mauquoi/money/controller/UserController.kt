package com.mauquoi.money.controller

import com.mauquoi.money.business.service.UserService
import com.mauquoi.money.model.UserPreferences
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping("/users")
class UserController @Inject constructor(private val userService: UserService) {

    @GetMapping("/{userId}/preferences")
    fun getPreferences(@PathVariable("userId") userId: Long): ResponseEntity<UserPreferences> {
        return ResponseEntity.ok(userService.getPreferences(userId))
    }

    @PutMapping("/{userId}/preferences")
    fun updatePreferences(@PathVariable("userId") userId: Long,
                          @RequestBody preferences: UserPreferences): ResponseEntity<Nothing> {
        userService.updatePreferences(userId, preferences)
        return ResponseEntity.noContent().build()
    }
}