package com.mercadopago.sdk.core.coremethods.domain.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SecurityCodeValidatorTest {

    @Test
    fun `given no expected length and a code with 3 or 4 digits when validated then there is no error`() {
        // Given
        val securityCodes = listOf("123", "1234")

        // When
        val errors = securityCodes.map { validateSecurityCode(securityCode = it, expectedLength = null) }

        // Then
        errors.forEach { assertNull(it) }
    }

    @Test
    fun `given no expected length and a code with 2 or 5 digits when validated then it is invalid`() {
        // Given
        val securityCodes = listOf("12", "12345")

        // When
        val errors = securityCodes.map { validateSecurityCode(securityCode = it, expectedLength = null) }

        // Then
        errors.forEach { assertEquals(CardValidationError.SECURITY_CODE_INVALID, it) }
    }

    @Test
    fun `given an expected length of 3 when validated then only 3 digits are accepted`() {
        // Given
        val expectedLength = 3

        // When
        val threeDigits = validateSecurityCode(securityCode = "123", expectedLength = expectedLength)
        val fourDigits = validateSecurityCode(securityCode = "1234", expectedLength = expectedLength)

        // Then
        assertNull(threeDigits)
        assertEquals(CardValidationError.SECURITY_CODE_INVALID, fourDigits)
    }

    @Test
    fun `given an expected length of 4 when validated then a 4 digit code is accepted`() {
        // Given
        val expectedLength = 4

        // When
        val fourDigits = validateSecurityCode(securityCode = "1234", expectedLength = expectedLength)
        val threeDigits = validateSecurityCode(securityCode = "123", expectedLength = expectedLength)

        // Then
        assertNull(fourDigits)
        assertEquals(CardValidationError.SECURITY_CODE_INVALID, threeDigits)
    }

    @Test
    fun `given an empty code or a code with letters when validated then it is invalid`() {
        // Given
        val securityCodes = listOf("", "12a", "1 23")

        // When
        val errors = securityCodes.map { validateSecurityCode(securityCode = it, expectedLength = null) }

        // Then
        errors.forEach { assertEquals(CardValidationError.SECURITY_CODE_INVALID, it) }
    }
}
