package com.mercadopago.sdk.core.coremethods.service

import kotlin.test.Test
import kotlin.test.assertEquals

internal class AmountFormatterTest {

    @Test
    fun `given a whole amount when formatted then it has two decimal places`() {
        // Given
        val amount = 5000.0

        // When
        val formatted = formatAmount(amount)

        // Then
        assertEquals("5000.00", formatted)
    }

    @Test
    fun `given an amount with one decimal place when formatted then it is padded with zero`() {
        // Given
        val amount = 100.5

        // When
        val formatted = formatAmount(amount)

        // Then
        assertEquals("100.50", formatted)
    }

    @Test
    fun `given a large amount when formatted then it has no exponent`() {
        // Given
        val amount = 10_000_000.0

        // When
        val formatted = formatAmount(amount)

        // Then
        assertEquals("10000000.00", formatted)
    }

    @Test
    fun `given an amount with floating point error when formatted then it is rounded to cents`() {
        // Given
        val amount = 0.1 + 0.2

        // When
        val formatted = formatAmount(amount)

        // Then
        assertEquals("0.30", formatted)
    }

    @Test
    fun `given the minimum amount when formatted then it keeps the cent`() {
        // Given
        val amount = 0.01

        // When
        val formatted = formatAmount(amount)

        // Then
        assertEquals("0.01", formatted)
    }

    @Test
    fun `given a negative amount below one unit when formatted then the sign comes before the units`() {
        // Given
        val amount = -0.5

        // When
        val formatted = formatAmount(amount)

        // Then
        assertEquals("-0.50", formatted)
    }

    @Test
    fun `given a negative amount with cents when formatted then the cents have no sign`() {
        // Given
        val amount = -1.05

        // When
        val formatted = formatAmount(amount)

        // Then
        assertEquals("-1.05", formatted)
    }
}
