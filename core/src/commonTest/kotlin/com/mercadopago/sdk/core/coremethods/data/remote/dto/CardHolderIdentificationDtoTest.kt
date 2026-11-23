package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class CardHolderIdentificationDtoTest {

    @Test
    fun `given an identification when converted to text then it is masked and no value appears`() {
        // Given
        val cardHolderIdentificationDto = CardHolderIdentificationDto(type = "CPF", number = "12345678909")

        // When
        val text = listOf(
            cardHolderIdentificationDto.toString(),
            listOf(cardHolderIdentificationDto).toString(),
        ).joinToString()

        // Then
        assertEquals("CardHolderIdentificationDto(***)", cardHolderIdentificationDto.toString())
        assertEquals("[CardHolderIdentificationDto(***)]", listOf(cardHolderIdentificationDto).toString())
        assertFalse(text.contains("CPF"))
        assertFalse(text.contains("12345678909"))
    }
}
