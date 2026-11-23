package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class CardTokenResponseDto(
    val id: String,
    @SerialName("last_four_digits") val lastFourDigits: String? = null,
) {
    override fun toString(): String = "CardTokenResponseDto(***)"
}
