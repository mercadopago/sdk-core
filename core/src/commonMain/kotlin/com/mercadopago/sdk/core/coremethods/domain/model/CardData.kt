package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Data of a new card to tokenize, as typed by the buyer in the secure fields.
 *
 * The values are read only by the core, to build the tokenization request. They are not exposed to the
 * integration, and [toString] never shows them.
 *
 * @param cardNumber Card number, digits only.
 * @param expirationDate Expiration date in the MM/YY or MM/YYYY format.
 * @param securityCode Security code (CVV), digits only.
 * @param cardHolder Holder of the card.
 */
public class CardData(
    internal val cardNumber: String,
    internal val expirationDate: String,
    internal val securityCode: String,
    internal val cardHolder: CardHolder,
) {
    override fun toString(): String = "CardData(***)"
}
