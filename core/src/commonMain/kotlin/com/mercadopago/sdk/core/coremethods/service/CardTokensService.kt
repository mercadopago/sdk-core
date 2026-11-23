package com.mercadopago.sdk.core.coremethods.service

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenRequestDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenResponseDto
import com.mercadopago.sdk.network.api.ApiBody
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.api.ApiMethod
import com.mercadopago.sdk.network.api.ApiRequest

private const val VERSION = "v1"
private const val CARD_TOKENS_PATH = "$VERSION/card_tokens"

internal class CardTokensService(
    private val apiClient: ApiClient,
) {
    suspend fun createCardToken(
        productId: String,
        sessionId: String,
        cardTokenRequestDto: CardTokenRequestDto,
    ): MPResult<CardTokenResponseDto, MPResultError> =
        apiClient.execute(
            ApiRequest(
                method = ApiMethod.POST,
                path = CARD_TOKENS_PATH,
                responseDeserializer = CardTokenResponseDto.serializer(),
                headers = cardTokensHeaders(productId = productId, sessionId = sessionId),
                body = ApiBody(value = cardTokenRequestDto, serializer = CardTokenRequestDto.serializer()),
            ),
        )
}
