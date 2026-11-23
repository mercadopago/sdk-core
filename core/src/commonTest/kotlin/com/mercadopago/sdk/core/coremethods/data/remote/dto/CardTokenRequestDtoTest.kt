package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
internal class CardTokenRequestDtoTest {

    // Same flags as NETWORK_JSON in :network (internal to that module). The field names come from @SerialName.
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val device = buildJsonObject {
        put("fingerprint_id", "device-fixture")
        putJsonArray("vendor_ids") { add(JsonPrimitive("vendor-fixture")) }
    }

    private val notSentKeys = listOf("esc", "session", "sdk_version", "processing_mode")

    @Test
    fun `given a new card with holder and device when encoded then it sends the new card fields`() {
        // Given
        val cardTokenRequestDto = CardTokenRequestDto(
            securityCode = "123",
            requireEsc = false,
            cardNumber = "4509953566233704",
            expirationMonth = 11,
            expirationYear = 2030,
            cardholder = CardHolderDto(
                name = "APRO",
                identification = CardHolderIdentificationDto(type = "CPF", number = "12345678909"),
            ),
            device = device,
        )

        // When
        val encoded = json.encodeToString(CardTokenRequestDto.serializer(), cardTokenRequestDto)

        // Then
        val expected = """
            {"card_number":"4509953566233704","security_code":"123","expiration_month":11,
            "expiration_year":2030,"cardholder":{"name":"APRO","identification":{"type":"CPF",
            "number":"12345678909"}},"require_esc":false,"device":{"fingerprint_id":"device-fixture",
            "vendor_ids":["vendor-fixture"]}}
        """.trimIndent()
        assertEquals(json.parseToJsonElement(expected), json.parseToJsonElement(encoded))
    }

    @Test
    fun `given a new card without identification and device when encoded then both are omitted`() {
        // Given
        val cardTokenRequestDto = CardTokenRequestDto(
            securityCode = "1234",
            requireEsc = false,
            cardNumber = "4509953566233704",
            expirationMonth = 1,
            expirationYear = 2031,
            cardholder = CardHolderDto(name = "APRO"),
        )

        // When
        val encoded = json.encodeToString(CardTokenRequestDto.serializer(), cardTokenRequestDto)

        // Then
        val expected = """
            {"card_number":"4509953566233704","security_code":"1234","expiration_month":1,
            "expiration_year":2031,"cardholder":{"name":"APRO"},"require_esc":false}
        """.trimIndent()
        assertEquals(json.parseToJsonElement(expected), json.parseToJsonElement(encoded))
    }

    @Test
    fun `given a saved card when encoded then it sends only the card id and security code and require_esc`() {
        // Given
        val cardTokenRequestDto = CardTokenRequestDto(
            securityCode = "123",
            requireEsc = false,
            cardId = "9876543210",
        )

        // When
        val encoded = json.encodeToString(CardTokenRequestDto.serializer(), cardTokenRequestDto)

        // Then
        val expected = """{"card_id":"9876543210","security_code":"123","require_esc":false}"""
        assertEquals(json.parseToJsonElement(expected), json.parseToJsonElement(encoded))
    }

    @Test
    fun `given require_esc false when encoded then the field is still sent`() {
        // Given
        val cardTokenRequestDto = CardTokenRequestDto(securityCode = "123", requireEsc = false, cardId = "1")

        // When
        val body = json.parseToJsonElement(
            json.encodeToString(CardTokenRequestDto.serializer(), cardTokenRequestDto),
        ).jsonObject

        // Then
        assertEquals(JsonPrimitive(false), body["require_esc"])
    }

    @Test
    fun `given both variants when encoded then esc and session and sdk_version and processing_mode are never sent`() {
        // Given
        val newCard = CardTokenRequestDto(
            securityCode = "123",
            requireEsc = false,
            cardNumber = "4509953566233704",
            expirationMonth = 11,
            expirationYear = 2030,
            cardholder = CardHolderDto(name = "APRO"),
            device = device,
        )
        val savedCard = CardTokenRequestDto(securityCode = "123", requireEsc = false, cardId = "9876543210")

        // When
        val bodies = listOf(newCard, savedCard).map {
            json.parseToJsonElement(json.encodeToString(CardTokenRequestDto.serializer(), it)).jsonObject
        }

        // Then
        bodies.forEach { body ->
            notSentKeys.forEach { key -> assertFalse(body.containsKey(key), key) }
        }
    }

    @Test
    fun `given a request with card data when converted to text then it is masked and no value appears`() {
        // Given
        val cardTokenRequestDto = CardTokenRequestDto(
            securityCode = "123",
            requireEsc = false,
            cardNumber = "4509953566233704",
            cardId = "9876543210",
            expirationMonth = 11,
            expirationYear = 2030,
            cardholder = CardHolderDto(
                name = "APRO",
                identification = CardHolderIdentificationDto(type = "CPF", number = "12345678909"),
            ),
            device = device,
        )

        // When
        val text = listOf(
            cardTokenRequestDto.toString(),
            listOf(cardTokenRequestDto).toString(),
            cardTokenRequestDto.cardholder.toString(),
            cardTokenRequestDto.cardholder?.identification.toString(),
        ).joinToString()

        // Then
        assertEquals("CardTokenRequestDto(***)", cardTokenRequestDto.toString())
        assertEquals("[CardTokenRequestDto(***)]", listOf(cardTokenRequestDto).toString())
        val values = listOf(
            "4509953566233704", "3704", "123", "9876543210", "11", "2030",
            "APRO", "CPF", "12345678909", "device-fixture",
        )
        values.forEach { value -> assertFalse(text.contains(value), value) }
        assertTrue(text.contains("CardHolderDto(***)"))
    }
}
