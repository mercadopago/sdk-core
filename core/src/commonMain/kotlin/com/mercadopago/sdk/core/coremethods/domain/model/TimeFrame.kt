package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Period in which an [Agreement] is valid, as sent by the API.
 *
 * @param startDate Start date in RFC 3339, such as "2026-01-01T00:00:00Z".
 * @param endDate End date in RFC 3339, such as "2026-12-31T23:59:59Z".
 */
public data class TimeFrame(
    val startDate: String,
    val endDate: String,
)
