package com.mercadopago.sdk.core.coremethods.data.remote.datasource

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentDto
import com.mercadopago.sdk.core.coremethods.service.InstallmentsService

internal class InstallmentsRemoteDataSource(
    private val installmentsService: InstallmentsService,
) {
    suspend fun getInstallments(
        productId: String,
        bin: String,
        amount: Double,
    ): MPResult<List<InstallmentDto>, MPResultError> =
        installmentsService.getInstallments(productId = productId, bin = bin, amount = amount)
}
