package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentDto
import com.mercadopago.sdk.core.coremethods.domain.model.Installment

internal fun InstallmentDto.toDomain(): Installment =
    Installment(
        paymentMethodId = paymentMethodId,
        paymentTypeId = paymentTypeId,
        thumbnail = thumbnail,
        issuer = issuer.toDomain(),
        processingMode = processingMode,
        merchantAccountId = merchantAccountId,
        payerCosts = payerCosts.map { it.toDomain() },
        agreements = agreements.map { it.toDomain() },
    )
