package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.core.coremethods.data.remote.datasource.PaymentMethodsRemoteDataSource
import com.mercadopago.sdk.core.coremethods.data.repository.PaymentMethodsRepositoryImpl
import com.mercadopago.sdk.core.coremethods.domain.repository.PaymentMethodsRepository
import com.mercadopago.sdk.core.coremethods.service.PaymentMethodsService
import com.mercadopago.sdk.network.api.ApiClient

internal fun createPaymentMethodsRepository(apiClient: ApiClient): PaymentMethodsRepository {
    val paymentMethodsService = PaymentMethodsService(apiClient = apiClient)
    return PaymentMethodsRepositoryImpl(
        remoteDataSource = PaymentMethodsRemoteDataSource(paymentMethodsService = paymentMethodsService),
    )
}
