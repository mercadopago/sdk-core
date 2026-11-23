package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Merchant account of an [Agreement].
 *
 * @param id Identifier of the merchant account.
 * @param paymentMethodOptionId Identifier of the payment method option. Empty when the API does not send it.
 */
public data class MerchantAccount(
    val id: String,
    val paymentMethodOptionId: String,
)
