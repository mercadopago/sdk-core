package com.mercadopago.sdk.core

private const val UUID_BYTE_COUNT = 16
private const val VERSION_BYTE_INDEX = 6
private const val VARIANT_BYTE_INDEX = 8
private const val VERSION_CLEAR_MASK = 0x0f
private const val VERSION_4_BITS = 0x40
private const val VARIANT_CLEAR_MASK = 0x3f
private const val VARIANT_RFC_4122_BITS = 0x80
private const val BYTE_MASK = 0xff
private const val HEX_RADIX = 16
private const val HEX_DIGITS_PER_BYTE = 2
private val UUID_GROUP_BOUNDS = listOf(0, 8, 12, 16, 20, 32)

internal fun generateSessionId(): String {
    val bytes = secureRandomBytes(UUID_BYTE_COUNT)
    bytes[VERSION_BYTE_INDEX] = ((bytes[VERSION_BYTE_INDEX].toInt() and VERSION_CLEAR_MASK) or VERSION_4_BITS).toByte()
    bytes[VARIANT_BYTE_INDEX] =
        ((bytes[VARIANT_BYTE_INDEX].toInt() and VARIANT_CLEAR_MASK) or VARIANT_RFC_4122_BITS).toByte()
    val hex = bytes.joinToString(separator = "") { byte ->
        (byte.toInt() and BYTE_MASK).toString(HEX_RADIX).padStart(HEX_DIGITS_PER_BYTE, '0')
    }
    return UUID_GROUP_BOUNDS.zipWithNext { start, end -> hex.substring(start, end) }.joinToString(separator = "-")
}
