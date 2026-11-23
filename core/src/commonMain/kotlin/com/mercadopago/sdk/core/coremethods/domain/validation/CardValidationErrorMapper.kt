package com.mercadopago.sdk.core.coremethods.domain.validation

import com.mercadopago.sdk.common.model.MPResultError

private const val INVALID_CARD_NUMBER_MESSAGE = "The card number is invalid"
private const val INVALID_EXPIRATION_DATE_FORMAT_MESSAGE = "The expiration date must be in the MM/YY or MM/YYYY format"
private const val EXPIRATION_MONTH_OUT_OF_RANGE_MESSAGE = "The expiration month must be from 01 to 12"
private const val CARD_EXPIRED_MESSAGE = "The card is expired"
private const val INVALID_SECURITY_CODE_MESSAGE = "The security code is invalid"

internal fun CardValidationError.toValidationError(): MPResultError.Validation =
    MPResultError.Validation(
        message = when (this) {
            CardValidationError.CARD_NUMBER_INVALID -> INVALID_CARD_NUMBER_MESSAGE
            CardValidationError.EXPIRATION_DATE_INVALID_FORMAT -> INVALID_EXPIRATION_DATE_FORMAT_MESSAGE
            CardValidationError.EXPIRATION_MONTH_OUT_OF_RANGE -> EXPIRATION_MONTH_OUT_OF_RANGE_MESSAGE
            CardValidationError.CARD_EXPIRED -> CARD_EXPIRED_MESSAGE
            CardValidationError.SECURITY_CODE_INVALID -> INVALID_SECURITY_CODE_MESSAGE
        },
    )
