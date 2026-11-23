package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal class CardHolderDto(
    val name: String,
    val identification: CardHolderIdentificationDto? = null,
) {
    override fun toString(): String = "CardHolderDto(***)"
}
