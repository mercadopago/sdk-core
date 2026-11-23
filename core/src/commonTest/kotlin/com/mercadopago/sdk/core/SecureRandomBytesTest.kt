package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class SecureRandomBytesTest {

    @Test
    fun `given a size when secureRandomBytes is called then it returns an array with that size`() {
        // Given
        val size = 16

        // When
        val bytes = secureRandomBytes(size)

        // Then
        assertEquals(size, bytes.size)
    }

    @Test
    fun `given two calls when secureRandomBytes is called then it returns different bytes`() {
        // Given
        val firstBytes = secureRandomBytes(16)

        // When
        val secondBytes = secureRandomBytes(16)

        // Then
        assertFalse(firstBytes.contentEquals(secondBytes))
    }
}
