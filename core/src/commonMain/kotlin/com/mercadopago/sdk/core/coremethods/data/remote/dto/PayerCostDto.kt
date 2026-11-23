package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class PayerCostDto(
    val installments: Int,
    val installmentAmount: Double,
    val installmentRate: Double,
    val totalAmount: Double,
    val minAllowedAmount: Double,
    val maxAllowedAmount: Double,
    val discountRate: Double = 0.0,
    val reimbursementRate: Double = 0.0,
    val installmentRateCollector: List<String>? = null,
    val labels: List<String>? = null,
    val formattedLabel: String = "",
    val paymentMethodOptionId: String = "",
)
