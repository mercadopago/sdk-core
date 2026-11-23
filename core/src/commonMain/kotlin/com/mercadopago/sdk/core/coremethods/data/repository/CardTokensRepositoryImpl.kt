package com.mercadopago.sdk.core.coremethods.data.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.DeviceInfoProvider
import com.mercadopago.sdk.core.coremethods.data.remote.datasource.CardTokensRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenRequestDto
import com.mercadopago.sdk.core.coremethods.data.remote.mapper.toCardTokenRequestDto
import com.mercadopago.sdk.core.coremethods.data.remote.mapper.toCardTokensSafeError
import com.mercadopago.sdk.core.coremethods.data.remote.mapper.toDomain
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.core.coremethods.domain.repository.CardTokensRepository

internal class CardTokensRepositoryImpl(
    private val cardTokensRemoteDataSource: CardTokensRemoteDataSource,
    private val deviceInfoProvider: DeviceInfoProvider?,
) : CardTokensRepository {

    override suspend fun createCardToken(
        productId: String,
        sessionId: String,
        cardData: CardData,
    ): MPResult<CardToken, MPResultError> =
        createCardToken(
            productId = productId,
            sessionId = sessionId,
            cardTokenRequestDto = cardData.toCardTokenRequestDto(deviceInfoProvider),
        )

    override suspend fun createCardToken(
        productId: String,
        sessionId: String,
        savedCardData: SavedCardData,
    ): MPResult<CardToken, MPResultError> =
        createCardToken(
            productId = productId,
            sessionId = sessionId,
            cardTokenRequestDto = savedCardData.toCardTokenRequestDto(deviceInfoProvider),
        )

    private suspend fun createCardToken(
        productId: String,
        sessionId: String,
        cardTokenRequestDto: CardTokenRequestDto,
    ): MPResult<CardToken, MPResultError> {
        val result = cardTokensRemoteDataSource.createCardToken(
            productId = productId,
            sessionId = sessionId,
            cardTokenRequestDto = cardTokenRequestDto,
        )
        return when (result) {
            is MPResult.Success -> MPResult.Success(result.data.toDomain())
            is MPResult.Failure -> MPResult.Failure(result.error.toCardTokensSafeError(cardTokenRequestDto))
        }
    }
}
