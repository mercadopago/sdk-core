package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Card token created by the core, to send in the payment instead of the card data.
 *
 * The API response has more fields. The core exposes only the token and the last four digits.
 *
 * [equals] and [hashCode] compare the instance, not the values, on purpose: the token never takes part
 * in comparisons or hashes. [toString] never shows the token nor the last four digits.
 *
 * @param token Identifier of the card token, sent in the payment.
 * @param lastFourDigits Last four digits of the card number, to show the card to the buyer.
 * Empty when the API does not return them.
 */
public class CardToken(
    public val token: String,
    public val lastFourDigits: String,
) {
    override fun toString(): String = "CardToken(***)"
}
