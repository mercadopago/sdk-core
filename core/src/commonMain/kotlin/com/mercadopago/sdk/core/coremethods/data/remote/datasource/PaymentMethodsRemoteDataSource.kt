package com.mercadopago.sdk.core.coremethods.data.remote.datasource

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto
import com.mercadopago.sdk.core.coremethods.service.PaymentMethodsService

internal class PaymentMethodsRemoteDataSource(
    private val paymentMethodsService: PaymentMethodsService,
) {
    suspend fun getPaymentMethods(productId: String, bin: String): MPResult<List<PaymentMethodDto>, MPResultError> =
        paymentMethodsService.getPaymentMethods(productId = productId, bin = bin)
}
