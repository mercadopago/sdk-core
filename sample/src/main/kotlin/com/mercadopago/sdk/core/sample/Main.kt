package com.mercadopago.sdk.core.sample

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.core.MercadoPagoCore
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val publicKey = System.getenv("MP_PUBLIC_KEY")?.takeIf { it.isNotBlank() }
        ?: error("Defina a variável MP_PUBLIC_KEY com uma chave de sandbox.")
    val core = MercadoPagoCore.Builder(publicKey = publicKey).build()
    try {
        when (val result = core.getIdentificationTypes()) {
            is MPResult.Success -> result.data.forEach { type ->
                println("${type.id} | ${type.name} | ${type.minLength}-${type.maxLength}")
            }
            is MPResult.Failure -> println("Falhou: ${result.error}")
        }
    } finally {
        core.close()
    }
}
