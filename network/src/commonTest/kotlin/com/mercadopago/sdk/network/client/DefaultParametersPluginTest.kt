package com.mercadopago.sdk.network.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DefaultParametersPluginTest {

    @Test
    fun `given null and empty default values when requested then they are not sent`() = runTest {
        // Given
        var sentQuery: String? = null
        var sentHeader: String? = null
        val engine = MockEngine { request ->
            sentQuery = request.url.parameters["site"]
            sentHeader = request.headers["X-Empty"]
            respond("{}", HttpStatusCode.OK)
        }
        val client = HttpClient(engine) {
            install(
                createDefaultParametersPlugin(
                    queryParametersProvider = { mapOf("public_key" to null, "site" to "") },
                    headersProvider = { mapOf("X-Empty" to "") },
                ),
            )
        }

        // When
        client.get("https://api.mercadopago.com/payments")

        // Then
        assertNull(sentQuery)
        assertNull(sentHeader)
    }

    @Test
    fun `given a public key default when requested then it is sent as query parameter and header`() = runTest {
        // Given
        var sentQuery: String? = null
        var sentHeader: String? = null
        val engine = MockEngine { request ->
            sentQuery = request.url.parameters["public_key"]
            sentHeader = request.headers["X-Public-Key"]
            respond("{}", HttpStatusCode.OK)
        }
        val client = HttpClient(engine) {
            install(
                createDefaultParametersPlugin(
                    queryParametersProvider = { mapOf("public_key" to "TEST-public-key") },
                    headersProvider = { mapOf("X-Public-Key" to "TEST-public-key") },
                ),
            )
        }

        // When
        client.get("https://api.mercadopago.com/payments")

        // Then
        assertEquals("TEST-public-key", sentQuery)
        assertEquals("TEST-public-key", sentHeader)
    }

    @Test
    fun `given a provider whose value changes when requested twice then each request sends the current value`() = runTest {
        // Given
        var currentKey = "TEST-first-key"
        val sentKeys = mutableListOf<String?>()
        val engine = MockEngine { request ->
            sentKeys.add(request.url.parameters["public_key"])
            respond("{}", HttpStatusCode.OK)
        }
        val client = HttpClient(engine) {
            install(
                createDefaultParametersPlugin(
                    queryParametersProvider = { mapOf("public_key" to currentKey) },
                    headersProvider = { emptyMap() },
                ),
            )
        }

        // When
        client.get("https://api.mercadopago.com/payments")
        currentKey = "TEST-second-key"
        client.get("https://api.mercadopago.com/payments")

        // Then
        assertEquals(listOf<String?>("TEST-first-key", "TEST-second-key"), sentKeys)
    }
}
