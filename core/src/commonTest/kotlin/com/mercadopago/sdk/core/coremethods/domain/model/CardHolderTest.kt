package com.mercadopago.sdk.core.coremethods.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

internal class CardHolderTest {

    @Test
    fun `given a card holder when converted to string then it shows neither the name nor the document`() {
        // Given
        val cardHolder = CardHolder(name = "APRO", identification = CardHolderIdentification(type = "CPF", number = "12345678909"))

        // When
        val text = cardHolder.toString()

        // Then
        assertFalse(text.contains("APRO"))
        assertFalse(text.contains("12345678909"))
    }

    @Test
    fun `given a card holder without identification when created then the identification is null`() {
        // Given
        val name = "APRO"

        // When
        val cardHolder = CardHolder(name = name)

        // Then
        assertEquals("APRO", cardHolder.name)
        assertNull(cardHolder.identification)
    }

    @Test
    fun `given a card holder inside a list when converted to string then it shows neither the name nor the document`() {
        // Given
        val list = listOf(CardHolder(name = "APRO", identification = CardHolderIdentification(type = "CPF", number = "12345678909")))

        // When
        val text = list.toString()

        // Then
        assertFalse(text.contains("APRO"))
        assertFalse(text.contains("CPF"))
        assertFalse(text.contains("12345678909"))
    }
}
