package com.mercadopago.sdk.core.coremethods.data.remote.datasource

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.IdentificationTypeDto
import com.mercadopago.sdk.core.coremethods.service.IdentificationTypesService

internal class IdentificationTypesRemoteDataSource(
    private val identificationTypesService: IdentificationTypesService,
) {
    suspend fun getIdentificationTypes(): MPResult<List<IdentificationTypeDto>, MPResultError> =
        identificationTypesService.getIdentificationTypes()
}
