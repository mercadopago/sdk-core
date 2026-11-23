package com.mercadopago.sdk.core.coremethods.domain.validation

internal enum class CardValidationError {
    CARD_NUMBER_INVALID,
    EXPIRATION_DATE_INVALID_FORMAT,
    EXPIRATION_MONTH_OUT_OF_RANGE,
    CARD_EXPIRED,
    SECURITY_CODE_INVALID,
}
