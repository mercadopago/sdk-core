package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CardInfoDto(
    val bin: Int,
    val length: CardLengthDto,
    val validation: String,
    val securityCode: SecurityCodeDto,
)
