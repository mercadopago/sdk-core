package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.core.FakeApiClient
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardInfoDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardLengthDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.SecurityCodeDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardInfo
import com.mercadopago.sdk.core.coremethods.domain.model.CardLength
import com.mercadopago.sdk.core.coremethods.domain.model.PaymentMethod
import com.mercadopago.sdk.core.coremethods.domain.model.SecurityCode
import com.mercadopago.sdk.network.api.ApiMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

internal class PaymentMethodsRepositoryTest {

    private val dto = PaymentMethodDto(
        id = "visa",
        paymentTypeId = "credit_card",
        status = "active",
        processingMode = "aggregator",
        merchantAccountId = "123",
        siteId = "MLA",
        thumbnail = null,
        card = CardInfoDto(
            bin = 411111,
            length = CardLengthDto(min = 16, max = 16),
            validation = "standard",
            securityCode = SecurityCodeDto(mode = "mandatory", location = "back", length = 3),
        ),
    )

    @Test
    fun `given a successful response when getPaymentMethods is called then it returns the mapped methods`() = runTest {
        // Given
        val repository = createPaymentMethodsRepository(FakeApiClient(MPResult.Success(listOf(dto))))

        // When
        val result = repository.getPaymentMethods(productId = "product", bin = "411111")

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
    fun `given a call when getPaymentMethods is called then it requests GET on the payment methods path`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<PaymentMethodDto>()))
        val repository = createPaymentMethodsRepository(apiClient)

        // When
        repository.getPaymentMethods(productId = "product", bin = "411111")

        // Then
        val request = apiClient.requests.single()
        assertEquals(ApiMethod.GET, request.method)
        assertEquals("cho-off/v1/payment_methods", request.path)
    }

    @Test
    fun `given an API error when getPaymentMethods is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Api(httpStatus = 400, code = "bad_request", message = "Invalid product")
        val repository = createPaymentMethodsRepository(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = repository.getPaymentMethods(productId = "product", bin = "411111")

        // Then
        assertEquals(MPResult.Failure(error), result)
    }

    @Test
    fun `given a network error when getPaymentMethods is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Network(NetworkErrorType.TIMEOUT)
        val repository = createPaymentMethodsRepository(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = repository.getPaymentMethods(productId = "product", bin = "411111")

        // Then
        assertEquals(MPResult.Failure(error), result)
    }

    @Test
    fun `given a first method without card when getPaymentMethods is called then it returns an incomplete card info error`() = runTest {
        // Given
        val repository = createPaymentMethodsRepository(FakeApiClient(MPResult.Success(listOf(dto.copy(card = null)))))

        // When
        val result = repository.getPaymentMethods(productId = "product", bin = "411111")

        // Then
        val expected = MPResultError.InvalidResponse(httpStatus = 200, reason = "INCOMPLETE_CARD_INFO", fieldPath = "[0].card")
        assertEquals(MPResult.Failure(expected), result)
    }

    @Test
    fun `given a mandatory security code with length 0 when getPaymentMethods is called then it returns an incomplete card info error`() = runTest {
        // Given
        val incompleteDto = dto.copy(
            card = dto.card?.copy(securityCode = SecurityCodeDto(mode = "mandatory", location = "back", length = 0)),
        )
        val repository = createPaymentMethodsRepository(FakeApiClient(MPResult.Success(listOf(incompleteDto))))

        // When
        val result = repository.getPaymentMethods(productId = "product", bin = "411111")

        // Then
        val expected = MPResultError.InvalidResponse(
            httpStatus = 200,
            reason = "INCOMPLETE_CARD_INFO",
            fieldPath = "[0].card.security_code.length",
        )
        assertEquals(MPResult.Failure(expected), result)
    }

    @Test
    fun `given a non-mandatory security code with length 0 when getPaymentMethods is called then it returns the methods`() = runTest {
        // Given
        val withoutCvvDto = dto.copy(
            card = dto.card?.copy(securityCode = SecurityCodeDto(mode = "optional", location = "", length = 0)),
        )
        val repository = createPaymentMethodsRepository(FakeApiClient(MPResult.Success(listOf(withoutCvvDto))))

        // When
        val result = repository.getPaymentMethods(productId = "product", bin = "411111")

        // Then
        assertIs<MPResult.Success<List<PaymentMethod>>>(result)
    }
}
