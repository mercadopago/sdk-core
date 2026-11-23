package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.core.DeviceInfoProvider
import com.mercadopago.sdk.core.coremethods.data.remote.datasource.CardTokensRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.repository.CardTokensRepositoryImpl
import com.mercadopago.sdk.core.coremethods.domain.repository.CardTokensRepository
import com.mercadopago.sdk.core.coremethods.service.CardTokensService
import com.mercadopago.sdk.network.api.ApiClient

internal fun createCardTokensRepository(
    apiClient: ApiClient,
    deviceInfoProvider: DeviceInfoProvider?,
): CardTokensRepository {
    val cardTokensService = CardTokensService(apiClient = apiClient)
    return CardTokensRepositoryImpl(
        cardTokensRemoteDataSource = CardTokensRemoteDataSource(cardTokensService = cardTokensService),
        deviceInfoProvider = deviceInfoProvider,
    )
}
