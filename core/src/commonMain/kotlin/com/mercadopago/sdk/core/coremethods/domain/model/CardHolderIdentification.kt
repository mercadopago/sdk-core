package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Identification document of a card holder.
 *
 * The values are read only by the core. [toString] never shows them.
 *
 * @param type Identifier of the document type, such as "CPF" or "DNI", as returned by
 * [com.mercadopago.sdk.core.MercadoPagoCore.getIdentificationTypes].
 * @param number Document number, as typed by the buyer.
 */
public class CardHolderIdentification(
    internal val type: String,
    internal val number: String,
) {
    override fun toString(): String = "CardHolderIdentification(***)"
}
