package com.mercadopago.sdk.core.coremethods.service

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentDto
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.api.ApiMethod
import com.mercadopago.sdk.network.api.ApiRequest
import kotlinx.serialization.builtins.ListSerializer

private const val CHO_OFF_API = "cho-off"
private const val VERSION = "v1"
private const val INSTALLMENTS_PATH = "$CHO_OFF_API/$VERSION/installments"
private const val PRODUCT_ID_PARAMETER = "product_id"
private const val BIN_PARAMETER = "bin"
private const val AMOUNT_PARAMETER = "amount"

internal class InstallmentsService(
    private val apiClient: ApiClient,
) {
    suspend fun getInstallments(
        productId: String,
        bin: String,
        amount: Double,
    ): MPResult<List<InstallmentDto>, MPResultError> =
        apiClient.execute(
            ApiRequest(
                method = ApiMethod.GET,
                path = INSTALLMENTS_PATH,
                responseDeserializer = ListSerializer(InstallmentDto.serializer()),
                queryParameters = mapOf(
                    PRODUCT_ID_PARAMETER to productId,
                    BIN_PARAMETER to bin,
                    AMOUNT_PARAMETER to formatAmount(amount),
                ),
            ),
        )
}
