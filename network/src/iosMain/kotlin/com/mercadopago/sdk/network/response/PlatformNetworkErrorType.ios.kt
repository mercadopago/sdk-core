package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.NetworkErrorType
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.Foundation.NSError
import platform.Foundation.NSURLErrorCannotConnectToHost
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDNSLookupFailed
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut

internal actual fun Throwable.platformNetworkErrorType(): NetworkErrorType? =
    (this as? DarwinHttpRequestException)?.let { it.origin.urlErrorType() }

private fun NSError.urlErrorType(): NetworkErrorType {
    if (domain != NSURLErrorDomain) return NetworkErrorType.UNKNOWN
    return when (code) {
        NSURLErrorTimedOut -> NetworkErrorType.TIMEOUT
        NSURLErrorNotConnectedToInternet,
        NSURLErrorCannotFindHost,
        NSURLErrorDNSLookupFailed,
        NSURLErrorCannotConnectToHost,
        NSURLErrorNetworkConnectionLost,
        -> NetworkErrorType.NO_CONNECTION

        else -> NetworkErrorType.UNKNOWN
    }
}
