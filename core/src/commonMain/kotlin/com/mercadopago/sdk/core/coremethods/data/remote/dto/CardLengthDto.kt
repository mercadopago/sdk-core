package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CardLengthDto(
    val min: Int,
    val max: Int,
)
