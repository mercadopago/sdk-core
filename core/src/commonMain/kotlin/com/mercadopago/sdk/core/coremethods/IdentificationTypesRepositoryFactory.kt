package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.core.coremethods.data.remote.datasource.IdentificationTypesRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.repository.IdentificationTypesRepositoryImpl
import com.mercadopago.sdk.core.coremethods.domain.repository.IdentificationTypesRepository
import com.mercadopago.sdk.core.coremethods.service.IdentificationTypesService
import com.mercadopago.sdk.network.api.ApiClient

internal fun createIdentificationTypesRepository(apiClient: ApiClient): IdentificationTypesRepository {
    val identificationTypesService = IdentificationTypesService(apiClient = apiClient)
    return IdentificationTypesRepositoryImpl(
        remoteDataSource = IdentificationTypesRemoteDataSource(identificationTypesService = identificationTypesService),
    )
}
