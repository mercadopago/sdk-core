package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.core.coremethods.data.remote.datasource.InstallmentsRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.repository.InstallmentsRepositoryImpl
import com.mercadopago.sdk.core.coremethods.domain.repository.InstallmentsRepository
import com.mercadopago.sdk.core.coremethods.service.InstallmentsService
import com.mercadopago.sdk.network.api.ApiClient

internal fun createInstallmentsRepository(apiClient: ApiClient): InstallmentsRepository {
    val installmentsService = InstallmentsService(apiClient = apiClient)
    return InstallmentsRepositoryImpl(
        remoteDataSource = InstallmentsRemoteDataSource(installmentsService = installmentsService),
    )
}
