package com.mercadopago.sdk.core.sample

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.MercadoPagoCore
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolder
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolderIdentification
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import kotlinx.coroutines.runBlocking

private const val TEST_CARD_NUMBER = "5031433215406351"
private const val TEST_CARD_EXPIRATION_DATE = "11/30"
private const val TEST_CARD_SECURITY_CODE = "123"
private const val TEST_CARD_HOLDER_NAME = "APRO"
private const val TEST_IDENTIFICATION_TYPE = "CPF"
private const val TEST_IDENTIFICATION_NUMBER = "12345678909"

fun main() = runBlocking {
    val publicKey = System.getenv("MP_PUBLIC_KEY")?.takeIf { it.isNotBlank() }
        ?: error("Defina a variável MP_PUBLIC_KEY com uma chave de sandbox.")
    val productId = System.getenv("MP_PRODUCT_ID")?.takeIf { it.isNotBlank() }
        ?: error("Defina a variável MP_PRODUCT_ID com o identificador do produto.")
    val bin = System.getenv("MP_BIN")?.takeIf { it.isNotBlank() }
        ?: error("Defina a variável MP_BIN com os primeiros dígitos de um cartão de sandbox.")
    val amount = System.getenv("MP_AMOUNT")?.takeIf { it.isNotBlank() }
        ?.let { it.toDoubleOrNull() ?: error("MP_AMOUNT deve ser um número, por exemplo 150.50.") }
        ?: 100.0
    val core = MercadoPagoCore.Builder(publicKey = publicKey, productId = productId).build()
    try {
        println("== identification_types ==")
        when (val result = core.getIdentificationTypes()) {
            is MPResult.Success -> result.data.forEach { type ->
                println("${type.id} | ${type.name} | ${type.minLength}-${type.maxLength}")
            }
            is MPResult.Failure -> println("Falhou identification_types: ${result.error}")
        }
        println("== payment_methods ==")
        val paymentMethodsResult = core.getPaymentMethods(bin = bin)
        when (paymentMethodsResult) {
            is MPResult.Success -> paymentMethodsResult.data.forEach { method ->
                println("${method.id} | ${method.paymentTypeId} | CVV ${method.card?.securityCode?.length}")
            }
            is MPResult.Failure -> println("Falhou payment_methods: ${paymentMethodsResult.error}")
        }
        val firstPaymentMethodId = (paymentMethodsResult as? MPResult.Success)?.data?.firstOrNull()?.id
        val paymentMethodId = System.getenv("MP_PAYMENT_METHOD_ID")?.takeIf { it.isNotBlank() }
            ?: firstPaymentMethodId
            ?: "visa"
        println("== card_issuers ($paymentMethodId) ==")
        when (val result = core.getCardIssuers(bin = bin, paymentMethodId = paymentMethodId)) {
            is MPResult.Success -> result.data.forEach { issuer ->
                println("${issuer.id} | ${issuer.name} | ${issuer.status}")
            }
            is MPResult.Failure -> println("Falhou card_issuers: ${result.error}")
        }
        println("== installments ($amount) ==")
        when (val result = core.getInstallments(bin = bin, amount = amount)) {
            is MPResult.Success -> result.data.forEach { installment ->
                installment.payerCosts.forEach { payerCost ->
                    println(
                        "${installment.paymentMethodId} | ${payerCost.installments}x " +
                            "${payerCost.installmentAmount} | total ${payerCost.totalAmount}",
                    )
                }
            }
            is MPResult.Failure -> println("Falhou installments: ${result.error}")
        }
        println("== card_tokens (cartão novo de teste) ==")
        val cardData = CardData(
            cardNumber = TEST_CARD_NUMBER,
            expirationDate = TEST_CARD_EXPIRATION_DATE,
            securityCode = TEST_CARD_SECURITY_CODE,
            cardHolder = CardHolder(
                name = TEST_CARD_HOLDER_NAME,
                identification = CardHolderIdentification(
                    type = TEST_IDENTIFICATION_TYPE,
                    number = TEST_IDENTIFICATION_NUMBER,
                ),
            ),
        )
        printCardTokenResult(result = core.createCardToken(cardData = cardData, securityCodeLength = null))
        println("== card_tokens (cartão salvo) ==")
        val savedCardId = System.getenv("MP_SAVED_CARD_ID")?.takeIf { it.isNotBlank() }
        val savedCardSecurityCode = System.getenv("MP_SAVED_CARD_SECURITY_CODE")?.takeIf { it.isNotBlank() }
        if (savedCardId == null || savedCardSecurityCode == null) {
            println("Pulado: defina MP_SAVED_CARD_ID e MP_SAVED_CARD_SECURITY_CODE para testar o cartão salvo.")
        } else {
            val savedCardData = SavedCardData(cardId = savedCardId, securityCode = savedCardSecurityCode)
            printCardTokenResult(
                result = core.createCardToken(savedCardData = savedCardData, securityCodeLength = null),
            )
        }
    } finally {
        core.close()
    }
}

private fun printCardTokenResult(result: MPResult<CardToken, MPResultError>) {
    when (result) {
        is MPResult.Success -> println("Sucesso: token criado.")
        is MPResult.Failure -> println("Falhou: ${result.error::class.simpleName}")
    }
}
