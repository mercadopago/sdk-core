package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class MerchantAccountDto(
    val id: String,
    val paymentMethodOptionId: String = "",
)
