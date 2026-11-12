package com.mercadopago.sdk.network.client

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

internal class NetworkJsonTest {

    @Test
    fun `given json with an unknown field when decoded then the known field is decoded`() {
        // Given
        val json = """{"id":"1234","unknown":true}"""

        // When
        val decoded = NETWORK_JSON.decodeFromString(Sample.serializer(), json)

        // Then
        assertEquals(Sample(id = "1234", note = null), decoded)
    }

    @Test
    fun `given a null field when encoded then the key is omitted`() {
        // Given
        val sample = Sample(id = "1234", note = null)

        // When
        val json = NETWORK_JSON.encodeToString(Sample.serializer(), sample)

        // Then
        assertEquals("""{"id":"1234"}""", json)
    }

    @Test
    fun `given a snake_case key when decoded then the camelCase property is set`() {
        // Given
        val json = """{"min_length":5}"""

        // When
        val decoded = NETWORK_JSON.decodeFromString(MinLengthSample.serializer(), json)

        // Then
        assertEquals(MinLengthSample(minLength = 5), decoded)
    }

    @Test
    fun `given a camelCase property when encoded then the key is snake_case`() {
        // Given
        val sample = MinLengthSample(minLength = 5)

        // When
        val json = NETWORK_JSON.encodeToString(MinLengthSample.serializer(), sample)

        // Then
        assertEquals("""{"min_length":5}""", json)
    }
}

@Serializable
private data class Sample(val id: String, val note: String?)

@Serializable
private data class MinLengthSample(val minLength: Int)
