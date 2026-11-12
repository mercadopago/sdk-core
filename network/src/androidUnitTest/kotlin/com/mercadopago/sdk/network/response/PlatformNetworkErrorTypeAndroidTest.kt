package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.NetworkErrorType
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlatformNetworkErrorTypeAndroidTest {

    @Test
    fun `given a socket timeout when mapped on android then the error type is timeout`() {
        // Given
        val failure = SocketTimeoutException("timed out")

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.TIMEOUT, type)
    }

    @Test
    fun `given an unknown host when mapped on android then the error type is no connection`() {
        // Given
        val failure = UnknownHostException("api.mercadopago.com")

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.NO_CONNECTION, type)
    }

    @Test
    fun `given a connect exception when mapped on android then the error type is no connection`() {
        // Given
        val failure = ConnectException("refused")

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.NO_CONNECTION, type)
    }
}
