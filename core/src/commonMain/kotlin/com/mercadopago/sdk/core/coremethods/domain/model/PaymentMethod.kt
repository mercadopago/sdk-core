package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Payment method that matches a card BIN, such as Visa or Mastercard.
 *
 * @param id Identifier of the payment method, such as "visa".
 * @param paymentTypeId Payment type, such as "credit_card".
 * @param status Status of the payment method.
 * @param processingMode Processing mode of the payment method.
 * @param merchantAccountId Identifier of the merchant account.
 * @param siteId Identifier of the site, such as "MLA".
 * @param thumbnail URL of the payment method logo, or null when the API does not send it.
 * @param card Card rules for the BIN. The first method of the list always has them. For the others, it is null when the API does not send them.
 */
public data class PaymentMethod(
    val id: String,
    val paymentTypeId: String,
    val status: String,
    val processingMode: String,
    val merchantAccountId: String,
    val siteId: String,
    val thumbnail: String?,
    val card: CardInfo?,
)
