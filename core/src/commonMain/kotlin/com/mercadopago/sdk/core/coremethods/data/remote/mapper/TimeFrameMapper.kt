package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.TimeFrameDto
import com.mercadopago.sdk.core.coremethods.domain.model.TimeFrame

internal fun TimeFrameDto.toDomain(): TimeFrame =
    TimeFrame(
        startDate = startDate,
        endDate = endDate,
    )
