package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal class CardTokenRequestDto(
    @SerialName("security_code") val securityCode: String,
    @SerialName("require_esc") val requireEsc: Boolean,
    @SerialName("card_number") val cardNumber: String? = null,
    @SerialName("card_id") val cardId: String? = null,
    @SerialName("expiration_month") val expirationMonth: Int? = null,
    @SerialName("expiration_year") val expirationYear: Int? = null,
    val cardholder: CardHolderDto? = null,
    val device: JsonObject? = null,
) {
    override fun toString(): String = "CardTokenRequestDto(***)"
}
