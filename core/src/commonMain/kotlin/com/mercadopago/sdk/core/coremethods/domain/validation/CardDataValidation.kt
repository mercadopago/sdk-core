package com.mercadopago.sdk.core.coremethods.domain.validation

import com.mercadopago.sdk.core.CalendarMonth
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData

internal fun validateNewCard(
    cardData: CardData,
    securityCodeLength: Int?,
    currentMonth: CalendarMonth,
): CardValidationError? =
    validateCardNumber(cardNumber = cardData.cardNumber)
        ?: validateExpirationDate(
            expirationDate = cardData.expirationDate,
            currentYear = currentMonth.year,
            currentMonth = currentMonth.month,
        )
        ?: validateSecurityCode(securityCode = cardData.securityCode, expectedLength = securityCodeLength)

internal fun validateSavedCard(savedCardData: SavedCardData, securityCodeLength: Int?): CardValidationError? =
    validateSecurityCode(securityCode = savedCardData.securityCode, expectedLength = securityCodeLength)
