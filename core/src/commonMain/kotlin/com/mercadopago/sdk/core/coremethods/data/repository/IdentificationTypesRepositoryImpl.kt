package com.mercadopago.sdk.core.coremethods.data.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.datasource.IdentificationTypesRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.remote.mapper.toDomain
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import com.mercadopago.sdk.core.coremethods.domain.repository.IdentificationTypesRepository
import com.mercadopago.sdk.core.model.map

internal class IdentificationTypesRepositoryImpl(
    private val remoteDataSource: IdentificationTypesRemoteDataSource,
) : IdentificationTypesRepository {

    override suspend fun getIdentificationTypes(): MPResult<List<IdentificationType>, MPResultError> =
        remoteDataSource.getIdentificationTypes().map { types -> types.map { it.toDomain() } }
}
