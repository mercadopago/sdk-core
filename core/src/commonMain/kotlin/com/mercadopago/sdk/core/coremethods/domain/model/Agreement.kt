package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Agreement between merchant accounts and a payment method, valid during a time frame.
 *
 * @param merchantAccounts Merchant accounts covered by the agreement.
 * @param timeFrame Period in which the agreement is valid.
 */
public data class Agreement(
    val merchantAccounts: List<MerchantAccount>,
    val timeFrame: TimeFrame,
)
