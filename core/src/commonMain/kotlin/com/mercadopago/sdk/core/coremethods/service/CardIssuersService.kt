package com.mercadopago.sdk.core.coremethods.service

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardIssuerDto
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.api.ApiMethod
import com.mercadopago.sdk.network.api.ApiRequest
import kotlinx.serialization.builtins.ListSerializer

private const val CHO_OFF_API = "cho-off"
private const val VERSION = "v1"
private const val CARD_ISSUERS_PATH = "$CHO_OFF_API/$VERSION/card_issuers"
private const val PRODUCT_ID_PARAMETER = "product_id"
private const val BIN_PARAMETER = "bin"
private const val PAYMENT_METHOD_ID_PARAMETER = "payment_method_id"

internal class CardIssuersService(
    private val apiClient: ApiClient,
) {
    suspend fun getCardIssuers(
        productId: String,
        bin: String,
        paymentMethodId: String,
    ): MPResult<List<CardIssuerDto>, MPResultError> =
        apiClient.execute(
            ApiRequest(
                method = ApiMethod.GET,
                path = CARD_ISSUERS_PATH,
                responseDeserializer = ListSerializer(CardIssuerDto.serializer()),
                queryParameters = mapOf(
                    PRODUCT_ID_PARAMETER to productId,
                    BIN_PARAMETER to bin,
                    PAYMENT_METHOD_ID_PARAMETER to paymentMethodId,
                ),
            ),
        )
}
