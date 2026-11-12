package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.network.client.NETWORK_JSON
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException

internal suspend fun <T> HttpResponse.toResult(
    deserializer: DeserializationStrategy<T>,
): MPResult<T, MPResultError> {
    if (!status.isSuccess()) {
        return MPResult.Failure(toApiError())
    }
    val body = bodyAsText()
    if (body.isBlank()) {
        return MPResult.Failure(
            MPResultError.InvalidResponse(httpStatus = status.value, reason = "EMPTY_BODY"),
        )
    }
    return try {
        MPResult.Success(NETWORK_JSON.decodeFromString(deserializer, body))
    } catch (e: SerializationException) {
        MPResult.Failure(
            MPResultError.InvalidResponse(httpStatus = status.value, reason = "DECODING_FAILED"),
        )
    }
}
