package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.NetworkErrorType
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.utils.io.errors.IOException

internal fun Throwable.networkErrorType(): NetworkErrorType? =
    when (this) {
        is HttpRequestTimeoutException,
        is ConnectTimeoutException,
        is SocketTimeoutException,
        -> NetworkErrorType.TIMEOUT

        else -> platformNetworkErrorType() ?: genericNetworkErrorType()
    }

private fun Throwable.genericNetworkErrorType(): NetworkErrorType? =
    if (this is IOException) NetworkErrorType.UNKNOWN else null
