package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.core.CalendarMonth
import com.mercadopago.sdk.core.DeviceClock
import com.mercadopago.sdk.core.FakeApiClient
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardInfoDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardLengthDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenResponseDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.IdentificationTypeDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.SecurityCodeDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolder
import com.mercadopago.sdk.core.coremethods.domain.model.CardInfo
import com.mercadopago.sdk.core.coremethods.domain.model.CardLength
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import com.mercadopago.sdk.core.coremethods.domain.model.PaymentMethod
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.core.coremethods.domain.model.SecurityCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

internal class CoreMethodsTest {

    @Test
    fun `given a successful response when getIdentificationTypes is called then it returns the types`() = runTest {
        // Given
        val dto = IdentificationTypeDto(id = "DNI", name = "DNI", type = "number", minLength = 7, maxLength = 8)
        val coreMethods = createCoreMethods(FakeApiClient(MPResult.Success(listOf(dto))), deviceInfoProvider = null)

        // When
        val result = coreMethods.getIdentificationTypes()

        // Then
        val expected = IdentificationType(id = "DNI", name = "DNI", type = "number", minLength = 7, maxLength = 8)
        assertEquals(MPResult.Success(listOf(expected)), result)
    }

    @Test
    fun `given a network error when getIdentificationTypes is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Network(NetworkErrorType.NO_CONNECTION)
        val coreMethods = createCoreMethods(FakeApiClient(MPResult.Failure(error)), deviceInfoProvider = null)

        // When
        val result = coreMethods.getIdentificationTypes()

        // Then
        assertEquals(MPResult.Failure(error), result)
    }

    @Test
    fun `given a successful response when getPaymentMethods is called then it returns the mapped methods`() = runTest {
        // Given
        val dto = PaymentMethodDto(
            id = "visa",
            paymentTypeId = "credit_card",
            status = "active",
            processingMode = "aggregator",
            merchantAccountId = "123",
            siteId = "MLA",
            card = CardInfoDto(
                bin = 411111,
                length = CardLengthDto(min = 16, max = 16),
                validation = "standard",
                securityCode = SecurityCodeDto(mode = "mandatory", location = "back", length = 3),
            ),
        )
        val coreMethods = createCoreMethods(FakeApiClient(MPResult.Success(listOf(dto))), deviceInfoProvider = null)

        // When
        val result = coreMethods.getPaymentMethods(productId = "product", bin = "411111")

        // Then
        val expected = PaymentMethod(
            id = "visa",
            paymentTypeId = "credit_card",
            status = "active",
            processingMode = "aggregator",
            merchantAccountId = "123",
            siteId = "MLA",
            thumbnail = null,
            card = CardInfo(
                bin = 411111,
                length = CardLength(min = 16, max = 16),
                validation = "standard",
                securityCode = SecurityCode(mode = "mandatory", location = "back", length = 3),
            ),
        )
        assertEquals(MPResult.Success(listOf(expected)), result)
    }

    @Test
    fun `given a network error when getPaymentMethods is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Network(NetworkErrorType.NO_CONNECTION)
        val coreMethods = createCoreMethods(FakeApiClient(MPResult.Failure(error)), deviceInfoProvider = null)

        // When
        val result = coreMethods.getPaymentMethods(productId = "product", bin = "411111")

        // Then
        assertEquals(MPResult.Failure(error), result)
    }

    @Test
    fun `given a successful response when createCardToken is called for a new card then it returns the card token`() = runTest {
        // Given
        val responseDto = CardTokenResponseDto(id = "tok-fixture-0001", lastFourDigits = "3704")
        val coreMethods = createCoreMethods(
            FakeApiClient(MPResult.Success(responseDto)),
            deviceInfoProvider = null,
            deviceClock = DeviceClock { CalendarMonth(year = 2026, month = 11) },
        )
        val cardData = CardData(
            cardNumber = "4509953566233704",
            expirationDate = "11/30",
            securityCode = "123",
            cardHolder = CardHolder(name = "APRO"),
        )

        // When
        val result = coreMethods.createCardToken(
            productId = "product",
            sessionId = "session",
            cardData = cardData,
            securityCodeLength = null,
        )

        // Then
        val cardToken = assertIs<MPResult.Success<CardToken>>(result).data
        assertEquals("tok-fixture-0001", cardToken.token)
        assertEquals("3704", cardToken.lastFourDigits)
    }

    @Test
    fun `given a network error when createCardToken is called for a saved card then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Network(NetworkErrorType.NO_CONNECTION)
        val coreMethods = createCoreMethods(FakeApiClient(MPResult.Failure(error)), deviceInfoProvider = null)
        val savedCardData = SavedCardData(cardId = "9876543210", securityCode = "123")

        // When
        val result = coreMethods.createCardToken(
            productId = "product",
            sessionId = "session",
            savedCardData = savedCardData,
            securityCodeLength = null,
        )

        // Then
        assertEquals(MPResult.Failure(error), result)
    }
}
