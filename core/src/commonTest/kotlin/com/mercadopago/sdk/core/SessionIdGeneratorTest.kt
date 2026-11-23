package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class SessionIdGeneratorTest {

    @Test
    fun `given the generator when generateSessionId is called then it returns a version 4 UUID`() {
        // Given
        val uuidPattern = Regex("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")

        // When
        val sessionId = generateSessionId()

        // Then
        assertTrue(uuidPattern.matches(sessionId))
    }

    @Test
    fun `given two calls when generateSessionId is called then it returns different ids`() {
        // Given
        val firstSessionId = generateSessionId()

        // When
        val secondSessionId = generateSessionId()

        // Then
        assertNotEquals(firstSessionId, secondSessionId)
    }
}
