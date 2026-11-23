package com.mercadopago.sdk.core.coremethods.data.repository

import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto

internal const val INCOMPLETE_CARD_INFO_REASON = "INCOMPLETE_CARD_INFO"

private const val SUCCESS_HTTP_STATUS = 200
private const val MANDATORY_SECURITY_CODE_MODE = "mandatory"

internal fun findIncompleteCardInfo(methods: List<PaymentMethodDto>): MPResultError.InvalidResponse? {
    val first = methods.firstOrNull() ?: return null
    val missingField = findMissingField(first) ?: return null
    return MPResultError.InvalidResponse(
        httpStatus = SUCCESS_HTTP_STATUS,
        reason = INCOMPLETE_CARD_INFO_REASON,
        fieldPath = "[0].$missingField",
    )
}

private fun findMissingField(method: PaymentMethodDto): String? {
    if (method.id.isEmpty()) return "id"
    val card = method.card ?: return "card"
    val securityCode = card.securityCode
    return when {
        card.length.min == 0 -> "card.length.min"
        card.length.max == 0 -> "card.length.max"
        securityCode.mode.isEmpty() -> "card.security_code.mode"
        securityCode.mode == MANDATORY_SECURITY_CODE_MODE && securityCode.length == 0 -> "card.security_code.length"
        securityCode.length > 0 && securityCode.location.isEmpty() -> "card.security_code.location"
        else -> null
    }
}
