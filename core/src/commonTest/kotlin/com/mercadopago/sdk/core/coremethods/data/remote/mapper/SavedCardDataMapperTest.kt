package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.DeviceInfoProvider
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenRequestDto
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
internal class SavedCardDataMapperTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private val savedCardData = SavedCardData(cardId = "9876543210", securityCode = "123")

    @Test
    fun `given a saved card with device when mapped then it builds the saved card body`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { """{"fingerprint_id":"device-fixture"}""" }

        // When
        val cardTokenRequestDto = savedCardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        val expected = """
            {"card_id":"9876543210","security_code":"123","require_esc":false,
            "device":{"fingerprint_id":"device-fixture"}}
        """.trimIndent()
        val encoded = json.encodeToString(CardTokenRequestDto.serializer(), cardTokenRequestDto)
        assertEquals(json.parseToJsonElement(expected), json.parseToJsonElement(encoded))
    }

    @Test
    fun `given a saved card without device provider when mapped then device is omitted`() {
        // Given
        val deviceInfoProvider: DeviceInfoProvider? = null

        // When
        val cardTokenRequestDto = savedCardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        val expected = """{"card_id":"9876543210","security_code":"123","require_esc":false}"""
        val encoded = json.encodeToString(CardTokenRequestDto.serializer(), cardTokenRequestDto)
        assertEquals(json.parseToJsonElement(expected), json.parseToJsonElement(encoded))
    }
}
