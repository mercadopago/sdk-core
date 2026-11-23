package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Installment options of a payment method for a BIN and an amount.
 *
 * @param paymentMethodId Identifier of the payment method, such as "visa".
 * @param paymentTypeId Type of the payment method, such as "credit_card".
 * @param thumbnail URL of the payment method logo. Empty when the API has no logo for it.
 * @param issuer Issuer of the card.
 * @param processingMode Processing mode of the payment method, such as "aggregator".
 * @param merchantAccountId Identifier of the merchant account. Usually empty.
 * @param payerCosts Installment options, in the order sent by the API.
 * @param agreements Agreements between the merchant accounts and the payment method. Usually empty.
 */
public data class Installment(
    val paymentMethodId: String,
    val paymentTypeId: String,
    val thumbnail: String,
    val issuer: InstallmentIssuer,
    val processingMode: String,
    val merchantAccountId: String,
    val payerCosts: List<PayerCost>,
    val agreements: List<Agreement>,
)
