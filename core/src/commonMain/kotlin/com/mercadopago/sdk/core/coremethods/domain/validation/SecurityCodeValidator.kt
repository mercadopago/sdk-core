package com.mercadopago.sdk.core.coremethods.domain.validation

private val ACCEPTED_LENGTHS_WITHOUT_EXPECTED = 3..4

internal fun validateSecurityCode(securityCode: String, expectedLength: Int?): CardValidationError? {
    if (securityCode.isEmpty() || !securityCode.all { it in '0'..'9' }) return CardValidationError.SECURITY_CODE_INVALID
    val isValidLength = if (expectedLength == null) {
        securityCode.length in ACCEPTED_LENGTHS_WITHOUT_EXPECTED
    } else {
        securityCode.length == expectedLength
    }
    return if (isValidLength) null else CardValidationError.SECURITY_CODE_INVALID
}
