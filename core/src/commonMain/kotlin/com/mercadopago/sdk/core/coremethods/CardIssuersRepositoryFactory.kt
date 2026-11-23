package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.core.coremethods.data.remote.datasource.CardIssuersRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.repository.CardIssuersRepositoryImpl
import com.mercadopago.sdk.core.coremethods.domain.repository.CardIssuersRepository
import com.mercadopago.sdk.core.coremethods.service.CardIssuersService
import com.mercadopago.sdk.network.api.ApiClient

internal fun createCardIssuersRepository(apiClient: ApiClient): CardIssuersRepository {
    val cardIssuersService = CardIssuersService(apiClient = apiClient)
    return CardIssuersRepositoryImpl(
        remoteDataSource = CardIssuersRemoteDataSource(cardIssuersService = cardIssuersService),
    )
}
