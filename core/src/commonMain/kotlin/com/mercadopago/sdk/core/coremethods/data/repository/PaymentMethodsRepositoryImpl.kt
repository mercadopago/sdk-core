package com.mercadopago.sdk.core.coremethods.data.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.datasource.PaymentMethodsRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto
import com.mercadopago.sdk.core.coremethods.data.remote.mapper.toDomain
import com.mercadopago.sdk.core.coremethods.domain.model.PaymentMethod
import com.mercadopago.sdk.core.coremethods.domain.repository.PaymentMethodsRepository

internal class PaymentMethodsRepositoryImpl(
    private val remoteDataSource: PaymentMethodsRemoteDataSource,
) : PaymentMethodsRepository {

    override suspend fun getPaymentMethods(
        productId: String,
        bin: String,
    ): MPResult<List<PaymentMethod>, MPResultError> =
        when (val response = remoteDataSource.getPaymentMethods(productId = productId, bin = bin)) {
            is MPResult.Failure -> MPResult.Failure(response.error)
            is MPResult.Success -> toResult(response.data.map { it.toDomain() }, response.data)
        }

    private fun toResult(
        methods: List<PaymentMethod>,
        dtos: List<PaymentMethodDto>,
    ): MPResult<List<PaymentMethod>, MPResultError> =
        findIncompleteCardInfo(dtos)?.let { MPResult.Failure(it) } ?: MPResult.Success(methods)
}
