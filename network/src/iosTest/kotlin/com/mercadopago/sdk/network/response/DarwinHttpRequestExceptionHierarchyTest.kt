package com.mercadopago.sdk.network.response

import io.ktor.client.engine.darwin.DarwinHttpRequestException
import io.ktor.utils.io.errors.IOException
import platform.Foundation.NSError
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorTimedOut
import kotlin.test.Test
import kotlin.test.assertIs

internal class DarwinHttpRequestExceptionHierarchyTest {

    @Test
    fun `given a darwin request exception when checked then it is an io exception`() {
        // Given
        val origin = NSError.errorWithDomain(NSURLErrorDomain, NSURLErrorTimedOut.toLong(), null)

        // When
        val failure: Throwable = DarwinHttpRequestException(origin)

        // Then
        assertIs<IOException>(failure)
    }
}
