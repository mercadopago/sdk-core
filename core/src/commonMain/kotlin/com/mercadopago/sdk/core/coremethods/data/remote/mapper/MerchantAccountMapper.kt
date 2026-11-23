package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.MerchantAccountDto
import com.mercadopago.sdk.core.coremethods.domain.model.MerchantAccount

internal fun MerchantAccountDto.toDomain(): MerchantAccount =
    MerchantAccount(
        id = id,
        paymentMethodOptionId = paymentMethodOptionId,
    )
