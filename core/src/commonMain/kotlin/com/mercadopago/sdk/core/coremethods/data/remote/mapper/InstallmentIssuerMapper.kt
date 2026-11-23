package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentIssuerDto
import com.mercadopago.sdk.core.coremethods.domain.model.InstallmentIssuer

internal fun InstallmentIssuerDto.toDomain(): InstallmentIssuer =
    InstallmentIssuer(
        id = id,
        thumbnail = thumbnail,
    )
