package com.mercadopago.sdk.core.coremethods.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class CardDataTest {

    private val cardData = CardData(
        cardNumber = "4111111111111111",
        expirationDate = "11/30",
        securityCode = "123",
        cardHolder = CardHolder(name = "APRO", identification = CardHolderIdentification(type = "CPF", number = "12345678909")),
    )

    @Test
    fun `given card data when converted to string then it shows none of the card values`() {
        // Given
        val sensitiveValues = listOf("4111111111111111", "411111", "1111", "11/30", "123", "APRO", "12345678909")

        // When
        val text = cardData.toString()

        // Then
        sensitiveValues.forEach { assertFalse(text.contains(it), "toString exposes a card value") }
    }

    @Test
    fun `given card data when read by the core then it keeps the values`() {
        // Given
        val data = cardData

        // When
        val values = listOf(data.cardNumber, data.expirationDate, data.securityCode, data.cardHolder.name)

        // Then
        assertEquals(listOf("4111111111111111", "11/30", "123", "APRO"), values)
    }

    @Test
    fun `given card data inside a list when converted to string then it shows none of the card values`() {
        // Given
        val list = listOf(cardData)

        // When
        val text = list.toString()

        // Then
        assertFalse(text.contains("4111111111111111"))
        assertFalse(text.contains("123"))
    }
}
