package com.mercadopago.sdk.core.coremethods.domain.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ExpirationDateValidatorTest {

    @Test
    fun `given a future date with a two digit year when validated then there is no error`() {
        // Given
        val expirationDate = "01/27"

        // When
        val error = validate(expirationDate)

        // Then
        assertNull(error)
    }

    @Test
    fun `given a future date with a four digit year when validated then there is no error`() {
        // Given
        val expirationDate = "12/2030"

        // When
        val error = validate(expirationDate)

        // Then
        assertNull(error)
    }

    @Test
    fun `given the current month when validated then there is no error`() {
        // Given
        val expirationDate = "11/26"

        // When
        val error = validate(expirationDate)

        // Then
        assertNull(error)
    }

    @Test
    fun `given the month before the current one when validated then the card is expired`() {
        // Given
        val expirationDate = "10/2026"

        // When
        val error = validate(expirationDate)

        // Then
        assertEquals(CardValidationError.CARD_EXPIRED, error)
    }

    @Test
    fun `given a past year when validated then the card is expired`() {
        // Given
        val expirationDate = "12/25"

        // When
        val error = validate(expirationDate)

        // Then
        assertEquals(CardValidationError.CARD_EXPIRED, error)
    }

    @Test
    fun `given month 1 and month 12 when validated then there is no error`() {
        // Given
        val expirationDates = listOf("01/27", "12/27")

        // When
        val errors = expirationDates.map { validate(it) }

        // Then
        errors.forEach { assertNull(it) }
    }

    @Test
    fun `given month 0 or month 13 when validated then the month is out of range`() {
        // Given
        val expirationDates = listOf("00/27", "13/27")

        // When
        val errors = expirationDates.map { validate(it) }

        // Then
        errors.forEach { assertEquals(CardValidationError.EXPIRATION_MONTH_OUT_OF_RANGE, it) }
    }

    @Test
    fun `given a date outside the MM-YY and MM-YYYY formats when validated then the format is invalid`() {
        // Given
        val expirationDates = listOf("", "1/27", "01-27", "0127", "ab/cd", "01/027", "01/27 ")

        // When
        val errors = expirationDates.map { validate(it) }

        // Then
        errors.forEach { assertEquals(CardValidationError.EXPIRATION_DATE_INVALID_FORMAT, it) }
    }

    private fun validate(expirationDate: String): CardValidationError? =
        validateExpirationDate(expirationDate = expirationDate, currentYear = CURRENT_YEAR, currentMonth = CURRENT_MONTH)

    private companion object {
        const val CURRENT_YEAR = 2026
        const val CURRENT_MONTH = 11
    }
}
