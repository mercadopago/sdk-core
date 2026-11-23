package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Accepted length range of the card number.
 *
 * @param min Minimum number of digits.
 * @param max Maximum number of digits.
 */
public data class CardLength(
    val min: Int,
    val max: Int,
)
