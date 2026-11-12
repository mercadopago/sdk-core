package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.NetworkErrorType
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.utils.io.errors.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TransportFailureMapperTest {

    @Test
    fun `given a request timeout when mapped then the error type is timeout`() {
        // Given
        val failure = HttpRequestTimeoutException("https://api.mercadopago.com/payments", 10_000L)

        // When
        val type = failure.networkErrorType()

        // Then
        assertEquals(NetworkErrorType.TIMEOUT, type)
    }

    @Test
    fun `given a connect timeout when mapped then the error type is timeout`() {
        // Given
        val failure = ConnectTimeoutException("connect timed out")

        // When
        val type = failure.networkErrorType()

        // Then
        assertEquals(NetworkErrorType.TIMEOUT, type)
    }

    @Test
    fun `given a socket timeout when mapped then the error type is timeout`() {
        // Given
        val failure = SocketTimeoutException("socket timed out")

        // When
        val type = failure.networkErrorType()

        // Then
        assertEquals(NetworkErrorType.TIMEOUT, type)
    }

    @Test
    fun `given a generic io failure when mapped then the error type is unknown`() {
        // Given
        val failure = IOException("io failure")

        // When
        val type = failure.networkErrorType()

        // Then
        assertEquals(NetworkErrorType.UNKNOWN, type)
    }

    @Test
    fun `given a non transport exception when mapped then no error type is returned`() {
        // Given
        val failure = IllegalStateException("bug")

        // When
        val type = failure.networkErrorType()

        // Then
        assertNull(type)
    }
}
