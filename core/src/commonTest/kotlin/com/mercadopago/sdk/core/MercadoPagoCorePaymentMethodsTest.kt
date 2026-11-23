package com.mercadopago.sdk.core

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class MercadoPagoCorePaymentMethodsTest {

    @Test
    fun `given a BIN with letters when getPaymentMethods is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<PaymentMethodDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getPaymentMethods(bin = "4567AB")

        // Then
        assertIs<MPResult.Failure<MPResultError.Validation>>(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a BIN with 5 digits when getPaymentMethods is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<PaymentMethodDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getPaymentMethods(bin = "45678")

        // Then
        assertIs<MPResult.Failure<MPResultError.Validation>>(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a BIN with 9 digits when getPaymentMethods is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<PaymentMethodDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getPaymentMethods(bin = "456789012")

        // Then
        assertIs<MPResult.Failure<MPResultError.Validation>>(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given an invalid BIN when getPaymentMethods is called then the validation message does not contain the BIN`() = runTest {
        // Given
        val core = createCore(FakeApiClient(MPResult.Success(emptyList<PaymentMethodDto>())))

        // When
        val result = core.getPaymentMethods(bin = "4567AB")

        // Then
        val error = assertIs<MPResult.Failure<MPResultError.Validation>>(result).error
        assertTrue(!error.message.contains("4567AB"))
    }

    @Test
    fun `given a valid 8-digit BIN when getPaymentMethods is called then it calls the API with the BIN`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<PaymentMethodDto>()))
        val core = createCore(apiClient)

        // When
        core.getPaymentMethods(bin = "45678901")

        // Then
        assertEquals(1, apiClient.requests.size)
    }

    private fun createCore(apiClient: FakeApiClient): MercadoPagoCore =
        MercadoPagoCore(apiClient = apiClient, config = CoreConfig(productId = "product"))
}
