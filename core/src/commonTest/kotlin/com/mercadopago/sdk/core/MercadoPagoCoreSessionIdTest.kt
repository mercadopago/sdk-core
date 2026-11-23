package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class MercadoPagoCoreSessionIdTest {

    @Test
    fun `given one core instance when the session id is read twice then it is the same`() {
        // Given
        val core = MercadoPagoCore.Builder(publicKey = "TEST-public-key", productId = "product").build()

        // When
        val firstRead = core.sessionId
        val secondRead = core.sessionId

        // Then
        assertEquals(firstRead, secondRead)
        core.close()
    }

    @Test
    fun `given two core instances when the session ids are read then they are different`() {
        // Given
        val builder = MercadoPagoCore.Builder(publicKey = "TEST-public-key", productId = "product")
        val firstCore = builder.build()
        val secondCore = builder.build()

        // When
        val firstSessionId = firstCore.sessionId
        val secondSessionId = secondCore.sessionId

        // Then
        assertNotEquals(firstSessionId, secondSessionId)
        firstCore.close()
        secondCore.close()
    }
}
