package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.PayerCostDto
import com.mercadopago.sdk.core.coremethods.domain.model.PayerCost

internal fun PayerCostDto.toDomain(): PayerCost =
    PayerCost(
        installments = installments,
        installmentAmount = installmentAmount,
        installmentRate = installmentRate,
        installmentRateCollector = installmentRateCollector.orEmpty(),
        totalAmount = totalAmount,
        minAllowedAmount = minAllowedAmount,
        maxAllowedAmount = maxAllowedAmount,
        discountRate = discountRate,
        reimbursementRate = reimbursementRate,
        labels = labels.orEmpty(),
        formattedLabel = formattedLabel,
        paymentMethodOptionId = paymentMethodOptionId,
    )
