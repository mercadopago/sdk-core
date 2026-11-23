package com.mercadopago.sdk.core.coremethods.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class CardHolderIdentificationTest {

    @Test
    fun `given an identification when converted to string then it shows neither the type nor the number`() {
        // Given
        val identification = CardHolderIdentification(type = "CPF", number = "12345678909")

        // When
        val text = identification.toString()

        // Then
        assertFalse(text.contains("CPF"))
        assertFalse(text.contains("12345678909"))
    }

    @Test
    fun `given an identification when read by the core then it keeps the values`() {
        // Given
        val identification = CardHolderIdentification(type = "CPF", number = "12345678909")

        // When
        val values = listOf(identification.type, identification.number)

        // Then
        assertEquals(listOf("CPF", "12345678909"), values)
    }

    @Test
    fun `given an identification inside a list when converted to string then it shows neither the type nor the number`() {
        // Given
        val list = listOf(CardHolderIdentification(type = "CPF", number = "12345678909"))

        // When
        val text = list.toString()

        // Then
        assertFalse(text.contains("CPF"))
        assertFalse(text.contains("12345678909"))
    }
}
