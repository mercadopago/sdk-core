package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.NetworkErrorType
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.Foundation.NSError
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlatformNetworkErrorTypeIosTest {

    @Test
    fun `given a timed out url error when mapped on ios then the error type is timeout`() {
        // Given
        val failure = DarwinHttpRequestException(NSError.errorWithDomain(NSURLErrorDomain, NSURLErrorTimedOut.toLong(), null))

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.TIMEOUT, type)
    }

    @Test
    fun `given a not connected url error when mapped on ios then the error type is no connection`() {
        // Given
        val failure = DarwinHttpRequestException(NSError.errorWithDomain(NSURLErrorDomain, NSURLErrorNotConnectedToInternet.toLong(), null))

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.NO_CONNECTION, type)
    }

    @Test
    fun `given an unmapped url error code when mapped on ios then the error type is unknown`() {
        // Given
        val failure = DarwinHttpRequestException(NSError.errorWithDomain(NSURLErrorDomain, 999L, null))

        // When
        val type = failure.platformNetworkErrorType()

        // Then
        assertEquals(NetworkErrorType.UNKNOWN, type)
    }
}
