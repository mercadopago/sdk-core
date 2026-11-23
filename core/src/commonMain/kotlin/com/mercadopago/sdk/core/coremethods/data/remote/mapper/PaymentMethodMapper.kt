package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto
import com.mercadopago.sdk.core.coremethods.domain.model.PaymentMethod

internal fun PaymentMethodDto.toDomain(): PaymentMethod =
    PaymentMethod(
        id = id,
        paymentTypeId = paymentTypeId,
        status = status,
        processingMode = processingMode,
        merchantAccountId = merchantAccountId,
        siteId = siteId,
        thumbnail = thumbnail,
        card = card?.toDomain(),
    )
