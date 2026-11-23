package com.mercadopago.sdk.core.coremethods.data.remote.datasource

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenRequestDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenResponseDto
import com.mercadopago.sdk.core.coremethods.service.CardTokensService

internal class CardTokensRemoteDataSource(
    private val cardTokensService: CardTokensService,
) {
    suspend fun createCardToken(
        productId: String,
        sessionId: String,
        cardTokenRequestDto: CardTokenRequestDto,
    ): MPResult<CardTokenResponseDto, MPResultError> =
        cardTokensService.createCardToken(
            productId = productId,
            sessionId = sessionId,
            cardTokenRequestDto = cardTokenRequestDto,
        )
}
