package com.mercadopago.sdk.core.coremethods.domain.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.domain.model.Installment

internal interface InstallmentsRepository {
    suspend fun getInstallments(
        productId: String,
        bin: String,
        amount: Double,
    ): MPResult<List<Installment>, MPResultError>
}
