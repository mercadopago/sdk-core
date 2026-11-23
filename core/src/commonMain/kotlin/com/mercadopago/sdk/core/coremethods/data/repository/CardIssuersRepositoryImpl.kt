package com.mercadopago.sdk.core.coremethods.data.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.datasource.CardIssuersRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.remote.mapper.toDomain
import com.mercadopago.sdk.core.coremethods.domain.model.CardIssuer
import com.mercadopago.sdk.core.coremethods.domain.repository.CardIssuersRepository

internal class CardIssuersRepositoryImpl(
    private val remoteDataSource: CardIssuersRemoteDataSource,
) : CardIssuersRepository {

    override suspend fun getCardIssuers(
        productId: String,
        bin: String,
        paymentMethodId: String,
    ): MPResult<List<CardIssuer>, MPResultError> =
        when (val response = remoteDataSource.getCardIssuers(productId, bin, paymentMethodId)) {
            is MPResult.Failure -> MPResult.Failure(response.error)
            is MPResult.Success -> MPResult.Success(response.data.map { it.toDomain() })
        }
}
