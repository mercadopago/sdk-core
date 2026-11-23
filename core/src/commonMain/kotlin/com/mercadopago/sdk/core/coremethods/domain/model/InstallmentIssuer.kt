package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Issuer of the card in an [Installment].
 *
 * @param id Identifier of the issuer, such as "24".
 * @param thumbnail URL of the issuer logo. Empty when the API has no logo for it.
 */
public data class InstallmentIssuer(
    val id: String,
    val thumbnail: String,
)
