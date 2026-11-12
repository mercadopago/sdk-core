package com.mercadopago.sdk.core.coremethods.domain.repository

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType

internal interface IdentificationTypesRepository {
    suspend fun getIdentificationTypes(): MPResult<List<IdentificationType>, MPResultError>
}
