package com.mercadopago.sdk.network.client

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.URLProtocol

internal fun createRequestDestinationGuard(allowedHost: String): ClientPlugin<Unit> =
    createClientPlugin("RequestDestinationGuard") {
        onRequest { request, _ ->
            val url = request.url
            val isAllowed = url.protocol == URLProtocol.HTTPS &&
                url.host == allowedHost &&
                url.user.isNullOrEmpty() &&
                url.password.isNullOrEmpty()
            check(isAllowed) { "Request destination is not the allowed HTTPS host" }
        }
    }
