package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.IdentificationTypeDto
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType

internal fun IdentificationTypeDto.toDomain(): IdentificationType =
    IdentificationType(
        id = id,
        name = name,
        type = type,
        minLength = minLength,
        maxLength = maxLength,
    )
