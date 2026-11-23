package com.mercadopago.sdk.core.coremethods.service

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.api.ApiMethod
import com.mercadopago.sdk.network.api.ApiRequest
import kotlinx.serialization.builtins.ListSerializer

private const val CHO_OFF_API = "cho-off"
private const val VERSION = "v1"
private const val PAYMENT_METHODS_PATH = "$CHO_OFF_API/$VERSION/payment_methods"
private const val PRODUCT_ID_PARAMETER = "product_id"
private const val BIN_PARAMETER = "bin"

internal class PaymentMethodsService(
    private val apiClient: ApiClient,
) {
    suspend fun getPaymentMethods(productId: String, bin: String): MPResult<List<PaymentMethodDto>, MPResultError> =
        apiClient.execute(
            ApiRequest(
                method = ApiMethod.GET,
                path = PAYMENT_METHODS_PATH,
                responseDeserializer = ListSerializer(PaymentMethodDto.serializer()),
                queryParameters = mapOf(PRODUCT_ID_PARAMETER to productId, BIN_PARAMETER to bin),
            ),
        )
}
