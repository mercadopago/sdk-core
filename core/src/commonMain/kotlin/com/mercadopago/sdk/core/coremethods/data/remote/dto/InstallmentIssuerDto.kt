package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class InstallmentIssuerDto(
    val id: String,
    val thumbnail: String = "",
)
