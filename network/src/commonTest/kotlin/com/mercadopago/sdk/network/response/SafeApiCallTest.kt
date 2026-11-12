package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.network.client.createHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class SafeApiCallTest {

    @Test
    fun `given a transport failure when the call runs then a network error is returned without the exception text`() = runTest {
        // Given
        val engine = MockEngine { throw IOException("https://api.mercadopago.com/payments?public_key=secret") }
        val client = createHttpClient(engine)

        // When
        val result = safeApiCall {
            client.get("payments").toResult(JsonObject.serializer())
        }

        // Then
        assertEquals(MPResult.Failure(MPResultError.Network(networkErrorType = NetworkErrorType.UNKNOWN)), result)
    }

    @Test
    fun `given a successful call when it runs then the result is returned unchanged`() = runTest {
        // Given
        val engine = MockEngine { respond("{}", HttpStatusCode.OK) }
        val client = createHttpClient(engine)

        // When
        val result = safeApiCall {
            client.get("payments").toResult(JsonObject.serializer())
        }

        // Then
        assertEquals(MPResult.Success(JsonObject(emptyMap())), result)
    }

    @Test
    fun `given a cancellation when the call runs then the cancellation is rethrown`() = runTest {
        // Given
        val cancellation = CancellationException("cancelled")

        // When
        val thrown = assertFailsWith<CancellationException> {
            safeApiCall<Unit> { throw cancellation }
        }

        // Then
        assertEquals(cancellation, thrown)
    }

    @Test
    fun `given an unexpected exception when the call runs then it is rethrown`() = runTest {
        // Given
        val unexpected = IllegalStateException("bug")

        // When
        val thrown = assertFailsWith<IllegalStateException> {
            safeApiCall<Unit> { throw unexpected }
        }

        // Then
        assertEquals(unexpected, thrown)
    }
}
