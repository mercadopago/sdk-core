package com.mercadopago.sdk.core

import java.security.SecureRandom

private val secureRandom = SecureRandom()

internal actual fun secureRandomBytes(size: Int): ByteArray =
    ByteArray(size).also(secureRandom::nextBytes)
