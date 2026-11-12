package com.mercadopago.sdk.network.client

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.network.response.toResult
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HttpClientFactoryTest {

    @Test
    fun `given a redirect response when requested then the redirect is not followed`() = runTest {
        // Given
        var engineCalls = 0
        val engine = MockEngine {
            engineCalls++
            respond("", HttpStatusCode.Found, headersOf("Location", "https://api.mercadopago.com/other"))
        }
        val client = createHttpClient(engine)

        // When
        val result = client.get("payments").toResult(JsonObject.serializer())

        // Then
        assertEquals(1, engineCalls)
        assertEquals(MPResult.Failure(MPResultError.Api(httpStatus = 302, code = "UNKNOWN_ERROR", message = "UNKNOWN_ERROR")), result)
    }
}
