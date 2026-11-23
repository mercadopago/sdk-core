package com.mercadopago.sdk.core.coremethods.domain.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.domain.model.CardIssuer

internal interface CardIssuersRepository {
    suspend fun getCardIssuers(
        productId: String,
        bin: String,
        paymentMethodId: String,
    ): MPResult<List<CardIssuer>, MPResultError>
}
