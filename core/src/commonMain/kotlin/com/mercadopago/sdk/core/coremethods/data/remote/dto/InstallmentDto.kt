package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class InstallmentDto(
    val paymentMethodId: String,
    val paymentTypeId: String,
    val issuer: InstallmentIssuerDto,
    val processingMode: String,
    val payerCosts: List<PayerCostDto>,
    val thumbnail: String = "",
    val merchantAccountId: String = "",
    val agreements: List<AgreementDto> = emptyList(),
)
