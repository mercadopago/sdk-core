package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal class CardHolderIdentificationDto(
    val type: String,
    val number: String,
) {
    override fun toString(): String = "CardHolderIdentificationDto(***)"
}
