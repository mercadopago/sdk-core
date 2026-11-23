package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * One installment option, such as 3 installments of 35.00.
 *
 * @param installments Number of installments.
 * @param installmentAmount Amount of each installment.
 * @param installmentRate Interest rate of the option, in percent.
 * @param installmentRateCollector Who receives the interest, such as "MERCADOPAGO".
 * @param totalAmount Total amount paid with this option.
 * @param minAllowedAmount Minimum amount accepted for this option.
 * @param maxAllowedAmount Maximum amount accepted for this option.
 * @param discountRate Discount rate of the option, in percent.
 * @param reimbursementRate Reimbursement rate of the option, in percent.
 * @param labels Labels of the option, such as the total financial cost.
 * @param formattedLabel Rates ready to show, such as "CFT: 12,50% TNA: 15,00%". Empty when the option has no rates to show.
 * @param paymentMethodOptionId Identifier of the payment method option. Empty when the API does not send it.
 */
public data class PayerCost(
    val installments: Int,
    val installmentAmount: Double,
    val installmentRate: Double,
    val installmentRateCollector: List<String>,
    val totalAmount: Double,
    val minAllowedAmount: Double,
    val maxAllowedAmount: Double,
    val discountRate: Double,
    val reimbursementRate: Double,
    val labels: List<String>,
    val formattedLabel: String,
    val paymentMethodOptionId: String,
)
