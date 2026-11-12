package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class IdentificationTypeDto(
    val id: String,
    val name: String,
    val type: String? = null,
    val minLength: Int,
    val maxLength: Int,
)
