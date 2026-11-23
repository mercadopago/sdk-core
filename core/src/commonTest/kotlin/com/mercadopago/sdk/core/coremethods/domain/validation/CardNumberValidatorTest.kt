package com.mercadopago.sdk.core.coremethods.domain.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class CardNumberValidatorTest {

    @Test
    fun `given card numbers with a valid Luhn checksum when validated then there is no error`() {
        // Given
        val cardNumbers = listOf("4111111111111111", "5555555555554444", "378282246310005")

        // When
        val errors = cardNumbers.map { validateCardNumber(it) }

        // Then
        errors.forEach { assertNull(it) }
    }

    @Test
    fun `given a card number with a wrong check digit when validated then it is invalid`() {
        // Given
        val cardNumber = "4111111111111112"

        // When
        val error = validateCardNumber(cardNumber)

        // Then
        assertEquals(CardValidationError.CARD_NUMBER_INVALID, error)
    }

    @Test
    fun `given an empty card number when validated then it is invalid`() {
        // Given
        val cardNumber = ""

        // When
        val error = validateCardNumber(cardNumber)

        // Then
        assertEquals(CardValidationError.CARD_NUMBER_INVALID, error)
    }

    @Test
    fun `given a card number with letters or spaces when validated then it is invalid`() {
        // Given
        val cardNumbers = listOf("4111a11111111111", "4111 1111 1111 1111")

        // When
        val errors = cardNumbers.map { validateCardNumber(it) }

        // Then
        errors.forEach { assertEquals(CardValidationError.CARD_NUMBER_INVALID, it) }
    }
}
