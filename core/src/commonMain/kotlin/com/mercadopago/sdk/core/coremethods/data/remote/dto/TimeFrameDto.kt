package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TimeFrameDto(
    val startDate: String,
    val endDate: String,
)
