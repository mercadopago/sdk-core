package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalSerializationApi::class)
internal class CardTokenResponseDtoTest {

    // Same flags as NETWORK_JSON in :network (internal to that module). The field names come from @SerialName.
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun `given a snake_case payload when decoded then it reads only the token and the last four digits`() {
        // Given
        val payload = """
            {"id":"tok-fixture-0001","public_key":"key-fixture","first_six_digits":"450995",
            "last_four_digits":"3704","expiration_month":11,"expiration_year":2030,"status":"active",
            "date_created":"2026-11-18T10:00:00.000-04:00","require_esc":false,
            "cardholder":{"name":"APRO","identification":{"type":"CPF","number":"12345678909"}},
            "luhn_validation":true,"liveMode":true,"card_number_length":16,"security_code_length":3}
        """.trimIndent()

        // When
        val cardTokenResponseDto = json.decodeFromString(CardTokenResponseDto.serializer(), payload)

        // Then
        assertEquals("tok-fixture-0001", cardTokenResponseDto.id)
        assertEquals("3704", cardTokenResponseDto.lastFourDigits)
    }

    @Test
    fun `given a payload without last_four_digits when decoded then the last four digits are null`() {
        // Given
        val payload = """{"id":"tok-fixture-0001","status":"active"}"""

        // When
        val cardTokenResponseDto = json.decodeFromString(CardTokenResponseDto.serializer(), payload)

        // Then
        assertEquals("tok-fixture-0001", cardTokenResponseDto.id)
        assertNull(cardTokenResponseDto.lastFourDigits)
    }

    @Test
    fun `given a payload with a null last_four_digits when decoded then the last four digits are null`() {
        // Given
        val payload = """{"id":"tok-fixture-0001","last_four_digits":null}"""

        // When
        val cardTokenResponseDto = json.decodeFromString(CardTokenResponseDto.serializer(), payload)

        // Then
        assertEquals("tok-fixture-0001", cardTokenResponseDto.id)
        assertNull(cardTokenResponseDto.lastFourDigits)
    }

    @Test
    fun `given a payload without id when decoded then it throws the SerializationException mapped to DECODING_FAILED`() {
        // Given
        val payload = """{"last_four_digits":"3704","status":"active"}"""

        // When
        val decode = { json.decodeFromString(CardTokenResponseDto.serializer(), payload) }

        // Then
        assertFailsWith<SerializationException> { decode() }
    }

    @Test
    fun `given a payload with a null id when decoded then it throws the SerializationException mapped to DECODING_FAILED`() {
        // Given
        val payload = """{"id":null,"last_four_digits":"3704","status":"active"}"""

        // When
        val decode = { json.decodeFromString(CardTokenResponseDto.serializer(), payload) }

        // Then
        assertFailsWith<SerializationException> { decode() }
    }

    @Test
    fun `given a response when converted to text then it is masked and neither the token nor the last four digits appear`() {
        // Given
        val cardTokenResponseDto = CardTokenResponseDto(id = "tok-fixture-0001", lastFourDigits = "3704")

        // When
        val text = listOf(cardTokenResponseDto.toString(), listOf(cardTokenResponseDto).toString()).joinToString()

        // Then
        assertEquals("CardTokenResponseDto(***)", cardTokenResponseDto.toString())
        assertEquals("[CardTokenResponseDto(***)]", listOf(cardTokenResponseDto).toString())
        assertFalse(text.contains("tok-fixture-0001"))
        assertFalse(text.contains("3704"))
    }
}
