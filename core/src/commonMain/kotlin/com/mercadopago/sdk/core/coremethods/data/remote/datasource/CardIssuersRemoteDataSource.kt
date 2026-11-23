package com.mercadopago.sdk.core.coremethods.data.remote.datasource

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardIssuerDto
import com.mercadopago.sdk.core.coremethods.service.CardIssuersService

internal class CardIssuersRemoteDataSource(
    private val cardIssuersService: CardIssuersService,
) {
    suspend fun getCardIssuers(
        productId: String,
        bin: String,
        paymentMethodId: String,
    ): MPResult<List<CardIssuerDto>, MPResultError> =
        cardIssuersService.getCardIssuers(productId = productId, bin = bin, paymentMethodId = paymentMethodId)
}
