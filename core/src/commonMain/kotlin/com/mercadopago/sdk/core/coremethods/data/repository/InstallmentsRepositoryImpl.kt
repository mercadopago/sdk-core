package com.mercadopago.sdk.core.coremethods.data.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.datasource.InstallmentsRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.remote.mapper.toDomain
import com.mercadopago.sdk.core.coremethods.domain.model.Installment
import com.mercadopago.sdk.core.coremethods.domain.repository.InstallmentsRepository

internal class InstallmentsRepositoryImpl(
    private val remoteDataSource: InstallmentsRemoteDataSource,
) : InstallmentsRepository {

    override suspend fun getInstallments(
        productId: String,
        bin: String,
        amount: Double,
    ): MPResult<List<Installment>, MPResultError> =
        when (val response = remoteDataSource.getInstallments(productId = productId, bin = bin, amount = amount)) {
            is MPResult.Failure -> MPResult.Failure(response.error)
            is MPResult.Success -> MPResult.Success(response.data.map { it.toDomain() })
        }
}
