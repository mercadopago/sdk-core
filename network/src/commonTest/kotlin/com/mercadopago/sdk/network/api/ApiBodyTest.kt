package com.mercadopago.sdk.network.api

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertFalse

internal class ApiBodyTest {

    @Test
    fun `given a body with card data when converted to text then the value is not exposed`() {
        // Given
        val cardNumber = "4111111111111111"
        val body = ApiBody(
            value = JsonObject(mapOf("card_number" to JsonPrimitive(cardNumber))),
            serializer = JsonObject.serializer(),
        )

        // When
        val text = body.toString()

        // Then
        assertFalse(text.contains(cardNumber))
    }
}
