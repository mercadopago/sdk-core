package com.mercadopago.sdk.core

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenResponseDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolder
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolderIdentification
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.network.api.ApiMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class MercadoPagoCoreCardTokensTest {

    private val validCardNumber = "4509953566233704"
    private val invalidCardNumber = "4509953566233705"
    private val validExpirationDate = "11/30"
    private val validSecurityCode = "123"
    private val invalidSecurityCode = "98"
    private val deviceClock = DeviceClock { CalendarMonth(year = 2026, month = 11) }
    private val responseDto = CardTokenResponseDto(id = "tok-fixture-0001", lastFourDigits = "3704")

    @Test
    fun `given an invalid number and expiration date and security code when createCardToken is called then it returns the card number error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(
            cardNumber = invalidCardNumber,
            expirationDate = "1130",
            securityCode = invalidSecurityCode,
        )

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = null)

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The card number is invalid")), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a card number with spaces when createCardToken is called then it returns the card number error`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(cardNumber = "4509 9535 6623 3704")

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = null)

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The card number is invalid")), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a valid number and an expiration date in another format when createCardToken is called then it returns the format error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(expirationDate = "1130", securityCode = invalidSecurityCode)

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = null)

        // Then
        val expected = MPResultError.Validation(message = "The expiration date must be in the MM/YY or MM/YYYY format")
        assertEquals(MPResult.Failure(expected), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a valid number and the expiration month 13 when createCardToken is called then it returns the month error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(expirationDate = "13/30", securityCode = invalidSecurityCode)

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = null)

        // Then
        val expected = MPResultError.Validation(message = "The expiration month must be from 01 to 12")
        assertEquals(MPResult.Failure(expected), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a valid number and the month before the current one when createCardToken is called then it returns the expired error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(expirationDate = "10/26", securityCode = invalidSecurityCode)

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = null)

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The card is expired")), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a valid number and date and an invalid security code when createCardToken is called then it returns the security code error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(expirationDate = "11/2026", securityCode = invalidSecurityCode)

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = null)

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The security code is invalid")), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given the current month as expiration when createCardToken is called then it calls the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(expirationDate = "11/26")

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = 3)

        // Then
        assertIs<MPResult.Success<CardToken>>(result)
        assertEquals(1, apiClient.requests.size)
    }

    @Test
    fun `given a security code length when the security code has another length then createCardToken returns the security code error`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(securityCode = "123")

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = 4)

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The security code is invalid")), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a security code length when the security code has that length then createCardToken calls the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(securityCode = "1234")

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = 4)

        // Then
        assertIs<MPResult.Success<CardToken>>(result)
        assertEquals(1, apiClient.requests.size)
    }

    @Test
    fun `given no security code length when the security code has 3 or 4 digits then createCardToken calls the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)

        // When
        val threeDigitsResult = core.createCardToken(cardData = createCardData(securityCode = "123"), securityCodeLength = null)
        val fourDigitsResult = core.createCardToken(cardData = createCardData(securityCode = "1234"), securityCodeLength = null)

        // Then
        assertIs<MPResult.Success<CardToken>>(threeDigitsResult)
        assertIs<MPResult.Success<CardToken>>(fourDigitsResult)
        assertEquals(2, apiClient.requests.size)
    }

    @Test
    fun `given no security code length when the security code has 5 digits then createCardToken returns the security code error`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val cardData = createCardData(securityCode = "98765")

        // When
        val result = core.createCardToken(cardData = cardData, securityCodeLength = null)

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The security code is invalid")), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a valid new card when createCardToken is called then it returns the card token from a single POST`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)

        // When
        val result = core.createCardToken(cardData = createCardData(), securityCodeLength = 3)

        // Then
        val cardToken = assertIs<MPResult.Success<CardToken>>(result).data
        assertEquals("tok-fixture-0001", cardToken.token)
        assertEquals("3704", cardToken.lastFourDigits)
        val request = apiClient.requests.single()
        assertEquals(ApiMethod.POST, request.method)
        assertEquals("v1/card_tokens", request.path)
    }

    @Test
    fun `given a valid saved card when createCardToken is called then it returns the card token from a single POST`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val savedCardData = SavedCardData(cardId = "9876543210", securityCode = validSecurityCode)

        // When
        val result = core.createCardToken(savedCardData = savedCardData, securityCodeLength = 3)

        // Then
        val cardToken = assertIs<MPResult.Success<CardToken>>(result).data
        assertEquals("tok-fixture-0001", cardToken.token)
        assertEquals("3704", cardToken.lastFourDigits)
        val request = apiClient.requests.single()
        assertEquals(ApiMethod.POST, request.method)
        assertEquals("v1/card_tokens", request.path)
    }

    @Test
    fun `given a saved card with an invalid security code when createCardToken is called then it returns the security code error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val savedCardData = SavedCardData(cardId = "9876543210", securityCode = "12a")

        // When
        val result = core.createCardToken(savedCardData = savedCardData, securityCodeLength = null)

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The security code is invalid")), result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a saved card when the security code length is given or not then createCardToken applies it`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = createCore(apiClient)
        val savedCardData = SavedCardData(cardId = "9876543210", securityCode = "1234")

        // When
        val withLengthResult = core.createCardToken(savedCardData = savedCardData, securityCodeLength = 3)
        val withoutLengthResult = core.createCardToken(savedCardData = savedCardData, securityCodeLength = null)

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The security code is invalid")), withLengthResult)
        assertIs<MPResult.Success<CardToken>>(withoutLengthResult)
        assertEquals(1, apiClient.requests.size)
    }

    @Test
    fun `given a failure from the API when createCardToken is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.InvalidResponse(httpStatus = 201, reason = "EMPTY_BODY")
        val core = createCore(FakeApiClient(MPResult.Failure(error)))

        // When
        val newCardResult = core.createCardToken(cardData = createCardData(), securityCodeLength = null)
        val savedCardResult = core.createCardToken(
            savedCardData = SavedCardData(cardId = "9876543210", securityCode = validSecurityCode),
            securityCodeLength = null,
        )

        // Then
        assertEquals(MPResult.Failure(error), newCardResult)
        assertEquals(MPResult.Failure(error), savedCardResult)
    }

    @Test
    fun `given invalid card data when createCardToken is called then no validation message contains the typed values`() = runTest {
        // Given
        val core = createCore(FakeApiClient(MPResult.Success(responseDto)))
        val typedValues = listOf(invalidCardNumber, "4509", "1130", "13/30", "10/26", invalidSecurityCode, "98765")
        val cardDataList = listOf(
            createCardData(cardNumber = invalidCardNumber),
            createCardData(expirationDate = "1130"),
            createCardData(expirationDate = "13/30"),
            createCardData(expirationDate = "10/26"),
            createCardData(securityCode = invalidSecurityCode),
            createCardData(securityCode = "98765"),
        )

        // When
        val messages = cardDataList.map { cardData ->
            val result = core.createCardToken(cardData = cardData, securityCodeLength = null)
            assertIs<MPResult.Failure<MPResultError.Validation>>(result).error.message
        }

        // Then
        messages.forEach { message ->
            typedValues.forEach { typedValue -> assertFalse(message.contains(typedValue)) }
        }
    }

    @Test
    fun `given a core built without device info provider when createCardToken is called then it validates without failing`() = runTest {
        // Given
        val core = MercadoPagoCore.Builder(publicKey = "TEST-public-key", productId = "product").build()

        // When
        val newCardResult = core.createCardToken(cardData = createCardData(cardNumber = invalidCardNumber), securityCodeLength = null)
        val savedCardResult = core.createCardToken(
            savedCardData = SavedCardData(cardId = "9876543210", securityCode = invalidSecurityCode),
            securityCodeLength = null,
        )

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The card number is invalid")), newCardResult)
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The security code is invalid")), savedCardResult)
        core.close()
    }

    @Test
    fun `given a config without device info provider when createCardToken is called then it returns the card token`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val config = CoreConfig(productId = "product", deviceInfoProvider = null)
        val core = MercadoPagoCore(apiClient = apiClient, config = config, deviceClock = deviceClock)

        // When
        val result = core.createCardToken(cardData = createCardData(), securityCodeLength = null)

        // Then
        assertIs<MPResult.Success<CardToken>>(result)
        assertEquals(1, apiClient.requests.size)
    }

    @Test
    fun `given the system clock when createCardToken is called with a far expiration date then it calls the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val core = MercadoPagoCore(apiClient = apiClient, config = CoreConfig(productId = "product"))

        // When
        val result = core.createCardToken(cardData = createCardData(expirationDate = "12/2099"), securityCodeLength = null)

        // Then
        assertIs<MPResult.Success<CardToken>>(result)
        assertEquals(1, apiClient.requests.size)
    }

    private fun createCore(apiClient: FakeApiClient): MercadoPagoCore =
        MercadoPagoCore(
            apiClient = apiClient,
            config = CoreConfig(
                productId = "product",
                deviceInfoProvider = DeviceInfoProvider { """{"fingerprint_id":"device-fixture"}""" },
            ),
            deviceClock = deviceClock,
        )

    private fun createCardData(
        cardNumber: String = validCardNumber,
        expirationDate: String = validExpirationDate,
        securityCode: String = validSecurityCode,
    ): CardData =
        CardData(
            cardNumber = cardNumber,
            expirationDate = expirationDate,
            securityCode = securityCode,
            cardHolder = CardHolder(
                name = "APRO",
                identification = CardHolderIdentification(type = "CPF", number = "12345678909"),
            ),
        )
}
