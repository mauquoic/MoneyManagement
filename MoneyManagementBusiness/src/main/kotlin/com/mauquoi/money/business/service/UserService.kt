package com.mauquoi.money.business.service

import com.mauquoi.money.model.UserPreferences
import com.mauquoi.money.repository.UserRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class UserService @Inject constructor(private val userRepository: UserRepository) {

    fun getPreferences(userId: Long): UserPreferences {
        return userRepository.findById(userId).get().preferences?: UserPreferences()
    }

    fun updatePreferences(userId: Long, preferences: UserPreferences) {
        val user = userRepository.findById(userId).get()
        val updatedUser = user.copy(preferences = preferences)
        userRepository.save(updatedUser)
    }
}