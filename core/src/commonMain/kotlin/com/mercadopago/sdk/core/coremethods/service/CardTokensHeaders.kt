package com.mercadopago.sdk.core.coremethods.service

import com.mercadopago.sdk.core.CORE_VERSION

private const val PRODUCT_ID_HEADER = "X-Product-id"
private const val SESSION_ID_HEADER = "Meli-Session-id"
private const val SDK_VERSION_HEADER = "SDK-version"

internal fun cardTokensHeaders(productId: String, sessionId: String): Map<String, String> =
    mapOf(
        PRODUCT_ID_HEADER to productId,
        SESSION_ID_HEADER to sessionId,
        SDK_VERSION_HEADER to CORE_VERSION,
    )
