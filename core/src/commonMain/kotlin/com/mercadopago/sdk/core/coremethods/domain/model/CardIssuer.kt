package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Card issuer, such as a bank, that accepts a payment method for a BIN.
 *
 * @param id Identifier of the issuer, such as "24".
 * @param name Name of the issuer, such as "Banco Galicia".
 * @param merchantAccountId Identifier of the merchant account.
 * @param processingMode Processing mode of the issuer.
 * @param status Status of the issuer.
 * @param thumbnail URL of the issuer logo.
 */
public data class CardIssuer(
    val id: String,
    val name: String,
    val merchantAccountId: String,
    val processingMode: String,
    val status: String,
    val thumbnail: String,
)
