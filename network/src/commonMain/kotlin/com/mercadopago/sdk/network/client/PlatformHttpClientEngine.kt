package com.mercadopago.sdk.network.client

import io.ktor.client.engine.HttpClientEngine

internal expect fun platformHttpClientEngine(): HttpClientEngine
