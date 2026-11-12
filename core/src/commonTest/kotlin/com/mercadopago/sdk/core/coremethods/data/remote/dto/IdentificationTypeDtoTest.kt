package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

@OptIn(ExperimentalSerializationApi::class)
internal class IdentificationTypeDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private val serializer = ListSerializer(IdentificationTypeDto.serializer())

    @Test
    fun `given a complete payload when decoded then it returns the identification types`() {
        // Given
        val payload = """[{"id":"CPF","name":"CPF","type":"number","min_length":11,"max_length":11}]"""

        // When
        val types = json.decodeFromString(serializer, payload)

        // Then
        assertEquals(listOf(IdentificationTypeDto("CPF", "CPF", "number", 11, 11)), types)
    }

    @Test
    fun `given extra fields in the payload when decoded then they are ignored`() {
        // Given
        val payload = """[{"id":"CPF","name":"CPF","type":"number","min_length":11,"max_length":11,"extra":"x"}]"""

        // When
        val types = json.decodeFromString(serializer, payload)

        // Then
        assertEquals(listOf(IdentificationTypeDto("CPF", "CPF", "number", 11, 11)), types)
    }

    @Test
    fun `given a payload without type when decoded then type is null`() {
        // Given
        val payload = """[{"id":"Otro","name":"Otro","min_length":3,"max_length":10}]"""

        // When
        val types = json.decodeFromString(serializer, payload)

        // Then
        assertNull(types.single().type)
    }

    @Test
    fun `given a payload without min_length when decoded then it fails naming the field`() {
        // Given
        val payload = """[{"id":"CPF","name":"CPF","type":"number","max_length":11}]"""

        // When
        val thrown = assertFailsWith<SerializationException> { json.decodeFromString(serializer, payload) }

        // Then
        assertContains(thrown.message.orEmpty(), "minLength")
    }

    @Test
    fun `given a null max_length when decoded then it fails`() {
        // Given
        val payload = """[{"id":"CPF","name":"CPF","type":"number","min_length":11,"max_length":null}]"""

        // When
        val thrown = assertFailsWith<SerializationException> { json.decodeFromString(serializer, payload) }

        // Then
        assertContains(thrown.message.orEmpty(), "max_length")
    }
}
