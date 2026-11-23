package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CardIssuerDto(
    val id: String,
    val name: String,
    val merchantAccountId: String,
    val processingMode: String,
    val status: String,
    val thumbnail: String,
)
