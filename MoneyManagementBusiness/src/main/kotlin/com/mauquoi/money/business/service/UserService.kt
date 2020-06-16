package com.mauquoi.money.business.service

import com.mauquoi.money.model.UserPreferences
import com.mauquoi.money.repository.UserRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class UserService @Inject constructor(private val userRepository: UserRepository) {

    fun getPreferences(userId: Long): UserPreferences {
        val user = userRepository.findById(userId).get()
        return if (user.preferences == null){
            val userWithPreferences = user.copy(preferences = UserPreferences())
            userRepository.save(userWithPreferences)
            UserPreferences()
        } else {
            userRepository.findById(userId).get().preferences?: UserPreferences()
        }
    }

    fun updatePreferences(userId: Long, preferences: UserPreferences) {
        val user = userRepository.findById(userId).get()
        val updatedUser = user.copy(preferences = preferences)
        userRepository.save(updatedUser)
    }
}