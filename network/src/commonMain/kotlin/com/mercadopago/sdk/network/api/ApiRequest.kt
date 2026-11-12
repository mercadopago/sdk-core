package com.mercadopago.sdk.network.api

import com.mercadopago.sdk.common.InternalMercadoPagoApi
import kotlinx.serialization.DeserializationStrategy

@InternalMercadoPagoApi
public class ApiRequest<T>(
    public val method: ApiMethod,
    public val path: String,
    internal val responseDeserializer: DeserializationStrategy<T>,
    internal val queryParameters: Map<String, String?> = emptyMap(),
    internal val headers: Map<String, String?> = emptyMap(),
    internal val body: ApiBody<*>? = null,
) {
    init {
        require(path.isRelativePath()) { "The path must be relative to the base URL" }
    }
}

private val ALLOWED_SEGMENT = Regex("[A-Za-z0-9._~-]+")

private fun String.isRelativePath(): Boolean {
    val segments = removePrefix("/").split("/")
    return segments.all { segment ->
        segment != "." && segment != ".." && ALLOWED_SEGMENT.matches(segment)
    }
}
