package com.mercadopago.sdk.network.api

import com.mercadopago.sdk.common.InternalMercadoPagoApi
import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError

@InternalMercadoPagoApi
public interface ApiClient {
    public suspend fun <T> execute(request: ApiRequest<T>): MPResult<T, MPResultError>

    public fun close()
}
