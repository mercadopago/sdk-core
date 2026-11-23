package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenRequestDto

private const val UNKNOWN_ERROR = "UNKNOWN_ERROR"
private const val CARD_TOKEN_REQUEST_FAILED = "Card token request failed"
private val IDENTIFIER_PATTERN = Regex("[A-Za-z0-9_.-]{1,64}")

internal fun MPResultError.toCardTokensSafeError(cardTokenRequestDto: CardTokenRequestDto): MPResultError =
    when (this) {
        is MPResultError.Api -> {
            val sensitiveValues = cardTokenRequestDto.sensitiveValues()
            MPResultError.Api(
                httpStatus = httpStatus,
                code = code.toSafeIdentifierOrNull(sensitiveValues) ?: UNKNOWN_ERROR,
                message = CARD_TOKEN_REQUEST_FAILED,
                errorCode = errorCode?.toSafeIdentifierOrNull(sensitiveValues),
                userErrorMessage = null,
            )
        }
        else -> this
    }

private fun CardTokenRequestDto.sensitiveValues(): List<String> =
    listOfNotNull(
        cardNumber,
        securityCode,
        cardId,
        cardholder?.name,
        cardholder?.identification?.number,
    ).filter { it.isNotBlank() }

private fun String.toSafeIdentifierOrNull(sensitiveValues: List<String>): String? =
    takeIf { identifier ->
        IDENTIFIER_PATTERN.matches(identifier) &&
            sensitiveValues.none { value -> identifier.contains(value, ignoreCase = true) }
    }
