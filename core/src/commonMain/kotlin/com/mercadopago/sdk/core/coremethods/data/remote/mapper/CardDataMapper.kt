package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.DeviceInfoProvider
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardHolderDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardHolderIdentificationDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenRequestDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolder
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolderIdentification
import com.mercadopago.sdk.core.readDeviceInfo

private val EXPIRATION_DATE_PATTERN = Regex("([0-9]{2})/([0-9]{2}|[0-9]{4})")
private const val MONTH_GROUP = 1
private const val YEAR_GROUP = 2
private const val TWO_DIGIT_YEAR_LENGTH = 2
private const val CENTURY = 2000

internal fun CardData.toCardTokenRequestDto(deviceInfoProvider: DeviceInfoProvider?): CardTokenRequestDto {
    val expirationDateMatch = EXPIRATION_DATE_PATTERN.matchEntire(expirationDate)
    return CardTokenRequestDto(
        securityCode = securityCode,
        requireEsc = false,
        cardNumber = cardNumber.filter { it in '0'..'9' },
        expirationMonth = expirationDateMatch?.groupValues?.get(MONTH_GROUP)?.toInt(),
        expirationYear = expirationDateMatch?.groupValues?.get(YEAR_GROUP)?.toFourDigitYear(),
        cardholder = cardHolder.toCardHolderDto(),
        device = readDeviceInfo(deviceInfoProvider),
    )
}

private fun String.toFourDigitYear(): Int =
    if (length == TWO_DIGIT_YEAR_LENGTH) CENTURY + toInt() else toInt()

private fun CardHolder.toCardHolderDto(): CardHolderDto =
    CardHolderDto(
        name = name,
        identification = identification?.toCardHolderIdentificationDto(),
    )

private fun CardHolderIdentification.toCardHolderIdentificationDto(): CardHolderIdentificationDto =
    CardHolderIdentificationDto(
        type = type,
        number = number,
    )
