package com.mercadopago.sdk.core.coremethods.domain.validation

private const val LUHN_MODULUS = 10
private const val MAX_DIGIT = 9

internal fun validateCardNumber(cardNumber: String): CardValidationError? =
    if (isValidLuhn(cardNumber)) null else CardValidationError.CARD_NUMBER_INVALID

private fun isValidLuhn(cardNumber: String): Boolean {
    if (cardNumber.isEmpty() || !cardNumber.all { it in '0'..'9' }) return false
    var checksum = 0
    for (index in cardNumber.length - 1 downTo 0 step 2) {
        checksum += cardNumber[index] - '0'
    }
    for (index in cardNumber.length - 2 downTo 0 step 2) {
        val doubled = (cardNumber[index] - '0') * 2
        checksum += if (doubled > MAX_DIGIT) doubled - MAX_DIGIT else doubled
    }
    return checksum % LUHN_MODULUS == 0
}
