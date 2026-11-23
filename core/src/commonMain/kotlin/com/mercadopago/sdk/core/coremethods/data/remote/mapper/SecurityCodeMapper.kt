package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.SecurityCodeDto
import com.mercadopago.sdk.core.coremethods.domain.model.SecurityCode

internal fun SecurityCodeDto.toDomain(): SecurityCode =
    SecurityCode(mode = mode, location = location, length = length)
