package com.mercadopago.sdk.core

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.api.ApiRequest

internal class FakeApiClient(
    private val response: MPResult<Any?, MPResultError>,
) : ApiClient {

    val requests = mutableListOf<ApiRequest<*>>()

    var closed = false
        private set

    override suspend fun <T> execute(request: ApiRequest<T>): MPResult<T, MPResultError> {
        requests += request
        @Suppress("UNCHECKED_CAST")
        return response as MPResult<T, MPResultError>
    }

    override fun close() {
        closed = true
    }
}
