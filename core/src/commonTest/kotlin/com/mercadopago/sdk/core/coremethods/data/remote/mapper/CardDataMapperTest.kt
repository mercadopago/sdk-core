package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.DeviceInfoProvider
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenRequestDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolder
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolderIdentification
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalSerializationApi::class)
internal class CardDataMapperTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private val deviceInfoProvider = DeviceInfoProvider { """{"fingerprint_id":"device-fixture"}""" }

    private fun cardData(
        cardNumber: String = "4509953566233704",
        expirationDate: String = "11/30",
        cardHolder: CardHolder = CardHolder(
            name = "APRO",
            identification = CardHolderIdentification(type = "CPF", number = "12345678909"),
        ),
    ): CardData =
        CardData(
            cardNumber = cardNumber,
            expirationDate = expirationDate,
            securityCode = "123",
            cardHolder = cardHolder,
        )

    private fun CardTokenRequestDto.encode(): JsonElement =
        json.parseToJsonElement(json.encodeToString(CardTokenRequestDto.serializer(), this))

    @Test
    fun `given a new card with holder and device when mapped then it builds the new card body`() {
        // Given
        val cardData = cardData()

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        val expected = """
            {"card_number":"4509953566233704","security_code":"123","expiration_month":11,
            "expiration_year":2030,"cardholder":{"name":"APRO","identification":{"type":"CPF",
            "number":"12345678909"}},"require_esc":false,"device":{"fingerprint_id":"device-fixture"}}
        """.trimIndent()
        assertEquals(json.parseToJsonElement(expected), cardTokenRequestDto.encode())
    }

    @Test
    fun `given an expiration date in MM-YY when mapped then the year has four digits`() {
        // Given
        val cardData = cardData(expirationDate = "01/31")

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        assertEquals(1, cardTokenRequestDto.expirationMonth)
        assertEquals(2031, cardTokenRequestDto.expirationYear)
    }

    @Test
    fun `given an expiration date in MM-YYYY when mapped then the year is kept`() {
        // Given
        val cardData = cardData(expirationDate = "12/2032")

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        assertEquals(12, cardTokenRequestDto.expirationMonth)
        assertEquals(2032, cardTokenRequestDto.expirationYear)
    }

    @Test
    fun `given an expiration date in an invalid format when mapped then month and year are omitted`() {
        // Given
        val cardData = cardData(expirationDate = "1130")

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        assertNull(cardTokenRequestDto.expirationMonth)
        assertNull(cardTokenRequestDto.expirationYear)
    }

    @Test
    fun `given a card number with separators when mapped then only the digits are sent`() {
        // Given
        val cardData = cardData(cardNumber = "4509 9535-6623 3704")

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        assertEquals("4509953566233704", cardTokenRequestDto.cardNumber)
    }

    @Test
    fun `given a holder without identification when mapped then identification is omitted`() {
        // Given
        val cardData = cardData(cardHolder = CardHolder(name = "APRO"))

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        val expected = """
            {"card_number":"4509953566233704","security_code":"123","expiration_month":11,
            "expiration_year":2030,"cardholder":{"name":"APRO"},"require_esc":false,
            "device":{"fingerprint_id":"device-fixture"}}
        """.trimIndent()
        assertEquals(json.parseToJsonElement(expected), cardTokenRequestDto.encode())
    }

    @Test
    fun `given no device provider when mapped then device is omitted`() {
        // Given
        val cardData = cardData()

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider = null)

        // Then
        assertNull(cardTokenRequestDto.device)
        assertFalse(cardTokenRequestDto.encode().toString().contains("device"))
    }

    @Test
    fun `given a provider with invalid JSON when mapped then device is omitted`() {
        // Given
        val cardData = cardData()
        val invalidDeviceInfoProvider = DeviceInfoProvider { "not json" }

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(invalidDeviceInfoProvider)

        // Then
        assertNull(cardTokenRequestDto.device)
    }

    @Test
    fun `given a new card when mapped then require_esc is false and the saved card id is not sent`() {
        // Given
        val cardData = cardData()

        // When
        val cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider)

        // Then
        assertEquals(false, cardTokenRequestDto.requireEsc)
        assertNull(cardTokenRequestDto.cardId)
        assertEquals(buildJsonObject { put("fingerprint_id", "device-fixture") }, cardTokenRequestDto.device)
    }
}
