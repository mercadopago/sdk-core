package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import io.ktor.utils.io.errors.IOException

internal suspend fun <T> safeApiCall(
    call: suspend () -> MPResult<T, MPResultError>,
): MPResult<T, MPResultError> =
    try {
        call()
    } catch (e: IOException) {
        MPResult.Failure(MPResultError.Network(networkErrorType = e.networkErrorType() ?: NetworkErrorType.UNKNOWN))
    }
