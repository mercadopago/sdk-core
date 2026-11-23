package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class SecurityCodeDto(
    val mode: String,
    val location: String,
    val length: Int,
)
