package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Card rules of a payment method for a BIN.
 *
 * @param bin BIN of the card.
 * @param length Accepted length of the card number.
 * @param validation Validation rule of the card number.
 * @param securityCode Rules of the security code (CVV).
 */
public data class CardInfo(
    val bin: Int,
    val length: CardLength,
    val validation: String,
    val securityCode: SecurityCode,
)
