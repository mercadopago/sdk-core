package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardLengthDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardLength

internal fun CardLengthDto.toDomain(): CardLength = CardLength(min = min, max = max)
