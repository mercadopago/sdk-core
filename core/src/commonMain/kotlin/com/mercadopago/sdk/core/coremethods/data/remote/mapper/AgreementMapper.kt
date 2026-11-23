package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.AgreementDto
import com.mercadopago.sdk.core.coremethods.domain.model.Agreement

internal fun AgreementDto.toDomain(): Agreement =
    Agreement(
        merchantAccounts = merchantAccounts.map { it.toDomain() },
        timeFrame = timeFrame.toDomain(),
    )
