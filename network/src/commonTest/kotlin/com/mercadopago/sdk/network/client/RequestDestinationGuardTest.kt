package com.mercadopago.sdk.network.client

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class RequestDestinationGuardTest {

    @Test
    fun `given a relative path when requested then it is sent to the base url host`() = runTest {
        // Given
        var requestedUrl: String? = null
        val engine = MockEngine { request ->
            requestedUrl = request.url.toString()
            respond("{}", HttpStatusCode.OK)
        }
        val client = createHttpClient(engine)

        // When
        val response: HttpResponse = client.get("payments")

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("https://api.mercadopago.com/payments", requestedUrl)
    }

    @Test
    fun `given an absolute url to a host outside the allowlist when requested then it is not sent`() = runTest {
        // Given
        var engineCalls = 0
        val engine = MockEngine {
            engineCalls++
            respond("{}", HttpStatusCode.OK)
        }
        val client = createHttpClient(engine)

        // When
        assertFailsWith<IllegalStateException> {
            client.get("https://evil.example.com/payments")
        }

        // Then
        assertEquals(0, engineCalls)
    }

    @Test
    fun `given an absolute http url to an allowed host when requested then it is sent over https to that host`() = runTest {
        // Given
        var requestedUrl: String? = null
        val engine = MockEngine { request ->
            requestedUrl = request.url.toString()
            respond("{}", HttpStatusCode.OK)
        }
        val client = createHttpClient(engine)

        // When
        client.get("http://api.mercadopago.com/payments")

        // Then
        assertEquals("https://api.mercadopago.com/payments", requestedUrl)
    }

    @Test
    fun `given a host with a misleading suffix when requested then it is not sent`() = runTest {
        // Given
        var engineCalls = 0
        val engine = MockEngine {
            engineCalls++
            respond("{}", HttpStatusCode.OK)
        }
        val client = createHttpClient(engine)

        // When
        assertFailsWith<IllegalStateException> {
            client.get("https://api.mercadopago.com.evil.example/payments")
        }

        // Then
        assertEquals(0, engineCalls)
    }

    @Test
    fun `given a url with user info when requested then it is not sent`() = runTest {
        // Given
        var engineCalls = 0
        val engine = MockEngine {
            engineCalls++
            respond("{}", HttpStatusCode.OK)
        }
        val client = createHttpClient(engine)

        // When
        assertFailsWith<IllegalStateException> {
            client.get("https://user:pass@api.mercadopago.com/payments")
        }

        // Then
        assertEquals(0, engineCalls)
    }
}
