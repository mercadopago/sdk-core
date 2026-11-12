package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.NetworkErrorType

internal expect fun Throwable.platformNetworkErrorType(): NetworkErrorType?
