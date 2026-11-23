package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenResponseDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken

internal fun CardTokenResponseDto.toDomain(): CardToken =
    CardToken(
        token = id,
        lastFourDigits = lastFourDigits.orEmpty(),
    )
