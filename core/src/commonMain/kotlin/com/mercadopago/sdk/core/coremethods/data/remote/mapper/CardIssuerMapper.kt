package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardIssuerDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardIssuer

internal fun CardIssuerDto.toDomain(): CardIssuer =
    CardIssuer(
        id = id,
        name = name,
        merchantAccountId = merchantAccountId,
        processingMode = processingMode,
        status = status,
        thumbnail = thumbnail,
    )
