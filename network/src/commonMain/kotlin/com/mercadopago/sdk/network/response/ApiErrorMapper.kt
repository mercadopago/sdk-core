package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.network.client.NETWORK_JSON
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

internal suspend fun HttpResponse.toApiError(): MPResultError.Api {
    val body = bodyAsText()
    val errorBody = parseJsonObjectOrNull(body)
    return MPResultError.Api(
        httpStatus = status.value,
        code = errorBody?.stringOrNull("code") ?: ResponseErrors.UNKNOWN_ERROR,
        message = errorBody?.stringOrNull("message") ?: ResponseErrors.UNKNOWN_ERROR,
        errorCode = errorBody?.stringOrNull("error_code"),
        userErrorMessage = errorBody?.stringOrNull("user_error_message"),
    )
}

private fun parseJsonObjectOrNull(body: String): JsonObject? =
    runCatching { NETWORK_JSON.parseToJsonElement(body) as? JsonObject }.getOrNull()

private fun JsonObject.stringOrNull(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull
