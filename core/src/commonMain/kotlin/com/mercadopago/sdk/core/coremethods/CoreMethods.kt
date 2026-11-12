package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import com.mercadopago.sdk.core.coremethods.domain.repository.IdentificationTypesRepository

internal class CoreMethods(
    private val identificationTypesRepository: IdentificationTypesRepository,
) {
    suspend fun getIdentificationTypes(): MPResult<List<IdentificationType>, MPResultError> =
        identificationTypesRepository.getIdentificationTypes()
}
