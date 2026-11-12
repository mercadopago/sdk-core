package com.mercadopago.sdk.core.coremethods.service

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.IdentificationTypeDto
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.api.ApiMethod
import com.mercadopago.sdk.network.api.ApiRequest
import kotlinx.serialization.builtins.ListSerializer

private const val CHO_OFF_API = "cho-off"
private const val VERSION = "v1"
private const val IDENTIFICATION_TYPES_PATH = "$CHO_OFF_API/$VERSION/identification_types"

internal class IdentificationTypesService(
    private val apiClient: ApiClient,
) {
    suspend fun getIdentificationTypes(): MPResult<List<IdentificationTypeDto>, MPResultError> =
        apiClient.execute(
            ApiRequest(
                method = ApiMethod.GET,
                path = IDENTIFICATION_TYPES_PATH,
                responseDeserializer = ListSerializer(IdentificationTypeDto.serializer()),
            ),
        )
}
