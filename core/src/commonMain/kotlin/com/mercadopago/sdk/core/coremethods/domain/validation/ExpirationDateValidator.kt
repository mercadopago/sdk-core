package com.mercadopago.sdk.core.coremethods.domain.validation

private val EXPIRATION_DATE_PATTERN = Regex("([0-9]{2})/([0-9]{2}|[0-9]{4})")
private const val FIRST_MONTH = 1
private const val LAST_MONTH = 12
private const val TWO_DIGIT_YEAR_LENGTH = 2
private const val CENTURY = 2000

internal fun validateExpirationDate(
    expirationDate: String,
    currentYear: Int,
    currentMonth: Int,
): CardValidationError? {
    val match = EXPIRATION_DATE_PATTERN.matchEntire(expirationDate)
        ?: return CardValidationError.EXPIRATION_DATE_INVALID_FORMAT
    val (monthText, yearText) = match.destructured
    val month = monthText.toInt()
    if (month !in FIRST_MONTH..LAST_MONTH) return CardValidationError.EXPIRATION_MONTH_OUT_OF_RANGE
    val year = if (yearText.length == TWO_DIGIT_YEAR_LENGTH) CENTURY + yearText.toInt() else yearText.toInt()
    val isExpired = year < currentYear || (year == currentYear && month < currentMonth)
    return if (isExpired) CardValidationError.CARD_EXPIRED else null
}
