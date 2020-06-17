package com.mauquoi.money.business.service

import com.mauquoi.money.business.util.TestObjectCreator
import com.mauquoi.money.model.User
import com.mauquoi.money.model.UserPreferences
import com.mauquoi.money.repository.UserRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    private lateinit var userService: UserService
    private val capturedUserId = slot<Long>()
    private val capturedUser = slot<User>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        userService = UserService(userRepository)
    }

    @Test
    fun getPreferences_doesNotContainPreferences_preferencesAreStored() {
        every { userRepository.findById(capture(capturedUserId)) } returns Optional.of(TestObjectCreator.createUser())
        every { userRepository.save(capture(capturedUser)) } returns TestObjectCreator.createUser()

        val preferences = userService.getPreferences(1L)

        verify(exactly = 1) { userRepository.findById(any()) }

        assertAll(
                { assertThat(capturedUserId.captured, `is`(1L)) },
                { assertThat(capturedUser.captured.preferences, `is`(UserPreferences())) },
                { assertThat(preferences, `is`(UserPreferences())) }
        )
    }

    @Test
    fun getPreferences_containsPreferences_preferencesAreReturned() {
        every { userRepository.findById(capture(capturedUserId)) } returns Optional.of(TestObjectCreator.createUserWithPreferences())

        val preferences = userService.getPreferences(2L)

        verify(exactly = 0) { userRepository.save<User>(any()) }

        assertAll(
                { assertThat(capturedUserId.captured, `is`(2L)) },
                { assertThat(preferences.locale, `is`(Locale.GERMAN)) }
        )
    }

    @Test
    fun updatePreferences() {
        every { userRepository.findById(capture(capturedUserId)) } returns Optional.of(TestObjectCreator.createUserWithPreferences())
        every { userRepository.save(capture(capturedUser)) } returns TestObjectCreator.createUser()

        userService.updatePreferences(3L, UserPreferences(1L, Locale.GERMAN, Currency.getInstance("USD")))

        verify(exactly = 1) { userRepository.save<User>(any()) }
        assertAll(
                { assertThat(capturedUserId.captured, `is`(3L)) },
                { assertThat(capturedUser.captured.preferences!!.locale, `is`(Locale.GERMAN)) }
        )

    }
}