package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class CardHolderDtoTest {

    @Test
    fun `given a card holder with name and identification when converted to text then it is masked and no value appears`() {
        // Given
        val cardHolderDto = CardHolderDto(
            name = "APRO",
            identification = CardHolderIdentificationDto(type = "CPF", number = "12345678909"),
        )

        // When
        val text = listOf(cardHolderDto.toString(), listOf(cardHolderDto).toString()).joinToString()

        // Then
        assertEquals("CardHolderDto(***)", cardHolderDto.toString())
        assertEquals("[CardHolderDto(***)]", listOf(cardHolderDto).toString())
        assertFalse(text.contains("APRO"))
        assertFalse(text.contains("12345678909"))
    }
}
