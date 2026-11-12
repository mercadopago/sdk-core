package com.mercadopago.sdk.network.client

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

internal fun createDefaultParametersPlugin(
    queryParametersProvider: () -> Map<String, String?>,
    headersProvider: () -> Map<String, String?>,
): ClientPlugin<Unit> =
    createClientPlugin("DefaultParametersPlugin") {
        onRequest { request, _ ->
            queryParametersProvider().forEach { (name, value) ->
                if (!value.isNullOrEmpty()) request.url.parameters.append(name, value)
            }
            headersProvider().forEach { (name, value) ->
                if (!value.isNullOrEmpty()) request.headers.append(name, value)
            }
        }
    }
