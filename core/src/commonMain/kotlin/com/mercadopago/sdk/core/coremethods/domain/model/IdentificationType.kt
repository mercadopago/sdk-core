package com.mercadopago.sdk.core.coremethods.domain.model

/**
 * Type of identification document accepted for payments (for example CPF or DNI).
 *
 * @param id Unique identifier of the identification type.
 * @param name Display name of the identification type.
 * @param type Category of the identification type, or null when the API does not send it.
 * @param minLength Minimum number of characters.
 * @param maxLength Maximum number of characters.
 */
public data class IdentificationType(
    val id: String,
    val name: String,
    val type: String?,
    val minLength: Int,
    val maxLength: Int,
)
