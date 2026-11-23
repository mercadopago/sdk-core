package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class AgreementDto(
    val timeFrame: TimeFrameDto,
    val merchantAccounts: List<MerchantAccountDto> = emptyList(),
)
