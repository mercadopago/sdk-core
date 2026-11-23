package com.mercadopago.sdk.core.coremethods.service

import com.mercadopago.sdk.core.CORE_VERSION
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CardTokensHeadersTest {

    @Test
    fun `given a product id and a session id when cardTokensHeaders is called then it returns the three headers`() {
        // Given
        val productId = "product-fixture"
        val sessionId = "123e4567-e89b-42d3-a456-426614174000"

        // When
        val headers = cardTokensHeaders(productId = productId, sessionId = sessionId)

        // Then
        val expected = mapOf(
            "X-Product-id" to productId,
            "Meli-Session-id" to sessionId,
            "SDK-version" to CORE_VERSION,
        )
        assertEquals(expected, headers)
    }
}
