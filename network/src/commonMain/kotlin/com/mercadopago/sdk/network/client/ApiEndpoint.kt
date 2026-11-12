package com.mercadopago.sdk.network.client

import io.ktor.http.Url

internal object ApiEndpoint {
    const val BASE_URL = "https://api.mercadopago.com/"
    val HOST: String = Url(BASE_URL).host
}
