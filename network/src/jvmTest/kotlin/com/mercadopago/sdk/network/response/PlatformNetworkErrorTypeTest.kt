package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.NetworkErrorType
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class PlatformNetworkErrorTypeTest {

    @Test
    fun `given an unknown host when mapped then the error type is no connection`() {
        // Given
        val failure = UnknownHostException("api.mercadopago.com")

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.NO_CONNECTION, type)
    }

    @Test
    fun `given a connect exception when mapped then the error type is no connection`() {
        // Given
        val failure = ConnectException("refused")

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.NO_CONNECTION, type)
    }

    @Test
    fun `given a java socket timeout when mapped then the error type is timeout`() {
        // Given
        val failure = SocketTimeoutException("timed out")

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.TIMEOUT, type)
    }

    @Test
    fun `given an unrelated exception when mapped then no platform error type is returned`() {
        // Given
        val failure = IllegalStateException("bug")

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertNull(type)
    }
}
