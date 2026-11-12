package com.mercadopago.sdk.network

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.api.ApiMethod
import com.mercadopago.sdk.network.api.ApiRequest
import com.mercadopago.sdk.network.client.NETWORK_JSON
import com.mercadopago.sdk.network.response.safeApiCall
import com.mercadopago.sdk.network.response.toResult
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent

internal class KtorApiClient(private val httpClient: HttpClient) : ApiClient {

    override suspend fun <T> execute(request: ApiRequest<T>): MPResult<T, MPResultError> =
        safeApiCall {
            httpClient.request {
                method = request.method.toHttpMethod()
                url(request.path)
                request.queryParameters.forEach { (name, value) ->
                    if (!value.isNullOrEmpty()) parameter(name, value)
                }
                request.headers.forEach { (name, value) ->
                    if (!value.isNullOrEmpty()) header(name, value)
                }
                request.body?.let { body ->
                    setBody(TextContent(body.encode(NETWORK_JSON), ContentType.Application.Json))
                }
            }.toResult(request.responseDeserializer)
        }

    override fun close() {
        httpClient.close()
        httpClient.engine.close()
    }
}

private fun ApiMethod.toHttpMethod(): HttpMethod =
    when (this) {
        ApiMethod.GET -> HttpMethod.Get
        ApiMethod.POST -> HttpMethod.Post
        ApiMethod.PUT -> HttpMethod.Put
        ApiMethod.DELETE -> HttpMethod.Delete
    }
