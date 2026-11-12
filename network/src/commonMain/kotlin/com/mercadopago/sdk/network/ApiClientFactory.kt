package com.mercadopago.sdk.network

import com.mercadopago.sdk.common.InternalMercadoPagoApi
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.client.createHttpClient
import com.mercadopago.sdk.network.client.platformHttpClientEngine

@InternalMercadoPagoApi
public fun createApiClient(
    defaultQueryParameters: () -> Map<String, String?> = { emptyMap() },
    defaultHeaders: () -> Map<String, String?> = { emptyMap() },
): ApiClient = KtorApiClient(createHttpClient(platformHttpClientEngine(), defaultQueryParameters, defaultHeaders))
