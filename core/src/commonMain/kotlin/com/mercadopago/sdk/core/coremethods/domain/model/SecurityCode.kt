package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Rules of the security code (CVV) of a payment method.
 *
 * @param mode Whether the security code is mandatory or optional.
 * @param location Where the security code is printed on the card, such as "back".
 * @param length Number of digits of the security code.
 */
public data class SecurityCode(
    val mode: String,
    val location: String,
    val length: Int,
)
