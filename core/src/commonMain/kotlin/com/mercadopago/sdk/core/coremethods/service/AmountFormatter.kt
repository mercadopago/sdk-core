package com.mercadopago.sdk.core.coremethods.service

import kotlin.math.abs
import kotlin.math.roundToLong

private const val CENTS_PER_UNIT = 100L
private const val CENTS_DIGITS = 2

internal fun formatAmount(amount: Double): String {
    val cents = (amount * CENTS_PER_UNIT).roundToLong()
    val sign = if (cents < 0) "-" else ""
    val absoluteCents = abs(cents)
    val units = absoluteCents / CENTS_PER_UNIT
    val fraction = (absoluteCents % CENTS_PER_UNIT).toString().padStart(CENTS_DIGITS, '0')
    return "$sign$units.$fraction"
}
