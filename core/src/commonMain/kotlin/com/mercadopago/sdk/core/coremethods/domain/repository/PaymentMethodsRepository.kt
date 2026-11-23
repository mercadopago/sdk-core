package com.mercadopago.sdk.core.coremethods.domain.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.domain.model.PaymentMethod

internal interface PaymentMethodsRepository {
    suspend fun getPaymentMethods(productId: String, bin: String): MPResult<List<PaymentMethod>, MPResultError>
}
