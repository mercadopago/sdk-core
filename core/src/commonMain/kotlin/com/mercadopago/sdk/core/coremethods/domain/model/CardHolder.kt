package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Holder of a card to tokenize.
 *
 * The values are read only by the core. [toString] never shows them.
 *
 * @param name Name of the holder, as printed on the card.
 * @param identification Identification document of the holder, when the site requires it.
 */
public class CardHolder(
    internal val name: String,
    internal val identification: CardHolderIdentification? = null,
) {
    override fun toString(): String = "CardHolder(***)"
}
