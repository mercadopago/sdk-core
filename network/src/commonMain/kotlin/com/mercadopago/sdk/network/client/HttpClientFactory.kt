package com.mercadopago.sdk.network.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType

private const val TIMEOUT_MILLIS = 10_000L

internal fun createHttpClient(
    engine: HttpClientEngine,
    defaultQueryParameters: () -> Map<String, String?> = { emptyMap() },
    defaultHeaders: () -> Map<String, String?> = { emptyMap() },
): HttpClient {
    return HttpClient(engine) {
        expectSuccess = false
        followRedirects = false
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT_MILLIS
            socketTimeoutMillis = TIMEOUT_MILLIS
            requestTimeoutMillis = TIMEOUT_MILLIS
        }
        install(createDefaultParametersPlugin(defaultQueryParameters, defaultHeaders))
        install(createRequestDestinationGuard(ApiEndpoint.HOST))
        defaultRequest {
            url(ApiEndpoint.BASE_URL)
            accept(ContentType.Application.Json)
        }
    }
}
