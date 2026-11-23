package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardInfoDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardInfo

internal fun CardInfoDto.toDomain(): CardInfo =
    CardInfo(
        bin = bin,
        length = length.toDomain(),
        validation = validation,
        securityCode = securityCode.toDomain(),
    )
