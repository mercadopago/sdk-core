package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Data of a saved card to tokenize again, with the security code typed by the buyer.
 *
 * The values are read only by the core, to build the tokenization request. They are not exposed to the
 * integration, and [toString] never shows them.
 *
 * @param cardId Identifier of the saved card.
 * @param securityCode Security code (CVV), digits only.
 */
public class SavedCardData(
    internal val cardId: String,
    internal val securityCode: String,
) {
    override fun toString(): String = "SavedCardData(***)"
}
