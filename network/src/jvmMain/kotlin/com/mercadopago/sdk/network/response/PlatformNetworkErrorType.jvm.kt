package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.NetworkErrorType
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal actual fun Throwable.platformNetworkErrorType(): NetworkErrorType? =
    when (this) {
        is SocketTimeoutException -> NetworkErrorType.TIMEOUT
        is UnknownHostException, is ConnectException -> NetworkErrorType.NO_CONNECTION
        else -> null
    }
