package com.mercadopago.sdk.core.coremethods.domain.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData

internal interface CardTokensRepository {
    suspend fun createCardToken(
        productId: String,
        sessionId: String,
        cardData: CardData,
    ): MPResult<CardToken, MPResultError>

    suspend fun createCardToken(
        productId: String,
        sessionId: String,
        savedCardData: SavedCardData,
    ): MPResult<CardToken, MPResultError>
}
