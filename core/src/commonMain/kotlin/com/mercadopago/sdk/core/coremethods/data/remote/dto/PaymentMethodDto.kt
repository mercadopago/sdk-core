package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentMethodDto(
    val id: String,
    val paymentTypeId: String,
    val status: String,
    val processingMode: String,
    val merchantAccountId: String,
    val siteId: String,
    val thumbnail: String? = null,
    val card: CardInfoDto? = null,
)
