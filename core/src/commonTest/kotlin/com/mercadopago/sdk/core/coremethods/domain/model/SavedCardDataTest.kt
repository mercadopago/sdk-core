package com.mercadopago.sdk.core.coremethods.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class SavedCardDataTest {

    @Test
    fun `given saved card data when converted to string then it shows neither the card id nor the security code`() {
        // Given
        val savedCardData = SavedCardData(cardId = "9876543210", securityCode = "4567")

        // When
        val text = savedCardData.toString()

        // Then
        assertFalse(text.contains("9876543210"))
        assertFalse(text.contains("4567"))
    }

    @Test
    fun `given saved card data when read by the core then it keeps the values`() {
        // Given
        val savedCardData = SavedCardData(cardId = "9876543210", securityCode = "4567")

        // When
        val values = listOf(savedCardData.cardId, savedCardData.securityCode)

        // Then
        assertEquals(listOf("9876543210", "4567"), values)
    }

    @Test
    fun `given saved card data inside a list when converted to string then it shows neither the card id nor the security code`() {
        // Given
        val list = listOf(SavedCardData(cardId = "9876543210", securityCode = "4567"))

        // When
        val text = list.toString()

        // Then
        assertFalse(text.contains("9876543210"))
        assertFalse(text.contains("4567"))
    }
}
