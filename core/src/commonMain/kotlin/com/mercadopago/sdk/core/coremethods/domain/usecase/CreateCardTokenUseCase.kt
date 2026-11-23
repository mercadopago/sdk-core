package com.mercadopago.sdk.core.coremethods.domain.usecase

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.DeviceClock
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.core.coremethods.domain.repository.CardTokensRepository
import com.mercadopago.sdk.core.coremethods.domain.validation.toValidationError
import com.mercadopago.sdk.core.coremethods.domain.validation.validateNewCard
import com.mercadopago.sdk.core.coremethods.domain.validation.validateSavedCard

internal class CreateCardTokenUseCase(
    private val cardTokensRepository: CardTokensRepository,
    private val deviceClock: DeviceClock,
) {
    suspend fun createNewCard(
        productId: String,
        sessionId: String,
        cardData: CardData,
        securityCodeLength: Int?,
    ): MPResult<CardToken, MPResultError> {
        val validationError = validateNewCard(
            cardData = cardData,
            securityCodeLength = securityCodeLength,
            currentMonth = deviceClock.currentCalendarMonth(),
        )
        if (validationError != null) {
            return MPResult.Failure(validationError.toValidationError())
        }
        return cardTokensRepository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)
    }

    suspend fun createSavedCard(
        productId: String,
        sessionId: String,
        savedCardData: SavedCardData,
        securityCodeLength: Int?,
    ): MPResult<CardToken, MPResultError> {
        val validationError = validateSavedCard(savedCardData = savedCardData, securityCodeLength = securityCodeLength)
        if (validationError != null) {
            return MPResult.Failure(validationError.toValidationError())
        }
        return cardTokensRepository.createCardToken(
            productId = productId,
            sessionId = sessionId,
            savedCardData = savedCardData,
        )
    }
}
