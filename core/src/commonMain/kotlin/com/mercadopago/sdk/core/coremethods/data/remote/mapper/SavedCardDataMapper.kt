package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.DeviceInfoProvider
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenRequestDto
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.core.readDeviceInfo

internal fun SavedCardData.toCardTokenRequestDto(deviceInfoProvider: DeviceInfoProvider?): CardTokenRequestDto =
    CardTokenRequestDto(
        securityCode = securityCode,
        requireEsc = false,
        cardId = cardId,
        device = readDeviceInfo(deviceInfoProvider),
    )
