package com.mercadopago.sdk.network.api

import kotlinx.serialization.json.JsonObject
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class ApiRequestTest {

    @Test
    fun `given a relative path when the request is created then it is accepted`() {
        // Given
        val path = "cho-off/v1/identification_types"

        // When
        val request = apiRequest(path)

        // Then
        assertEquals(path, request.path)
    }

    @Test
    fun `given an absolute url when the request is created then it is rejected`() {
        // Given
        val path = "https://evil.example.com/payments"

        // When
        assertFailsWith<IllegalArgumentException> { apiRequest(path) }

        // Then
    }

    @Test
    fun `given a protocol relative path when the request is created then it is rejected`() {
        // Given
        val path = "//evil.example.com/payments"

        // When
        assertFailsWith<IllegalArgumentException> { apiRequest(path) }

        // Then
    }

    @Test
    fun `given a backslash path when the request is created then it is rejected`() {
        // Given
        val paths = listOf("\\\\evil.example.com/payments", "/\\evil.example.com", "payments\\..")

        // When
        paths.forEach { path ->
            assertFailsWith<IllegalArgumentException>("path $path") { apiRequest(path) }
        }

        // Then
    }

    @Test
    fun `given a path with dot segments when the request is created then it is rejected`() {
        // Given
        val path = "v1/../../payments"

        // When
        assertFailsWith<IllegalArgumentException> { apiRequest(path) }

        // Then
    }

    @Test
    fun `given a path with a query or an authority character when the request is created then it is rejected`() {
        // Given
        val paths = listOf("payments?public_key=abc", "user@evil.example.com", "payments#fragment")

        // When
        paths.forEach { path ->
            assertFailsWith<IllegalArgumentException>("path $path") { apiRequest(path) }
        }

        // Then
    }

    private fun apiRequest(path: String) =
        ApiRequest(
            method = ApiMethod.GET,
            path = path,
            responseDeserializer = JsonObject.serializer(),
        )
}
