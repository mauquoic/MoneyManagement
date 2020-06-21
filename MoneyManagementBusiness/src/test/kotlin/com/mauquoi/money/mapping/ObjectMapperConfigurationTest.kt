package com.mauquoi.money.mapping

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ObjectMapperConfigurationTest {

    private val objectMapper = ObjectMapperConfiguration().defaultObjectMapper()

    @Test
    fun defaultObjectMapper_mapsDatesCorrectly() {
        val testObject = TestObject(LocalDate.of(2020, 1, 1))
        assertThat(objectMapper.writeValueAsString(testObject), `is`("{\"date\":\"2020-01-01\"}"))
    }

    @Test
    fun defaultObjectMapper_doesNotSerializeNulls() {
        val testObject = TestObject()
        assertThat(objectMapper.writeValueAsString(testObject), `is`("{}"))
    }

    @Test
    fun defaultObjectMapper_doesNotSerializeEmptyLists() {
        val testObjectNotEmpty = TestListObject(listOf("Test"))
        assertThat(objectMapper.writeValueAsString(testObjectNotEmpty), `is`("{\"list\":[\"Test\"]}"))
        val testObject = TestListObject()
        assertThat(objectMapper.writeValueAsString(testObject), `is`("{}"))
    }

    private data class TestObject(val date: LocalDate? = null)

    private data class TestListObject(val list: List<String>? = emptyList())
}