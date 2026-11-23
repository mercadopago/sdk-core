package com.mercadopago.sdk.core

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentDto
import kotlinx.coroutines.test.runTest
import kotlin.math.nextUp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class MercadoPagoCoreInstallmentsTest {

    @Test
    fun `given a BIN with letters when getInstallments is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getInstallments(bin = "4567AB", amount = 100.0)

        // Then
        assertValidationError(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a zero amount when getInstallments is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getInstallments(bin = "45678901", amount = 0.0)

        // Then
        assertValidationError(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a negative zero amount when getInstallments is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getInstallments(bin = "45678901", amount = -0.0)

        // Then
        assertValidationError(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a negative amount when getInstallments is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getInstallments(bin = "45678901", amount = -10.0)

        // Then
        assertValidationError(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given half a cent when getInstallments is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getInstallments(bin = "45678901", amount = 0.005)

        // Then
        assertValidationError(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given the minimum amount when getInstallments is called then it calls the API once`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        core.getInstallments(bin = "45678901", amount = 0.01)

        // Then
        assertEquals(1, apiClient.requests.size)
    }

    @Test
    fun `given the maximum amount when getInstallments is called then it calls the API once`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        core.getInstallments(bin = "45678901", amount = MAXIMUM_AMOUNT)

        // Then
        assertEquals(1, apiClient.requests.size)
    }

    @Test
    fun `given the first amount above the maximum when getInstallments is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getInstallments(bin = "45678901", amount = MAXIMUM_AMOUNT.nextUp())

        // Then
        assertValidationError(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a non finite amount when getInstallments is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        val nanResult = core.getInstallments(bin = "45678901", amount = Double.NaN)
        val positiveInfinityResult = core.getInstallments(bin = "45678901", amount = Double.POSITIVE_INFINITY)
        val negativeInfinityResult = core.getInstallments(bin = "45678901", amount = Double.NEGATIVE_INFINITY)

        // Then
        assertValidationError(nanResult)
        assertValidationError(positiveInfinityResult)
        assertValidationError(negativeInfinityResult)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a valid BIN and amount when getInstallments is called then it calls the API once`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val core = createCore(apiClient)

        // When
        core.getInstallments(bin = "45678901", amount = 100.0)

        // Then
        assertEquals(1, apiClient.requests.size)
    }

    @Test
    fun `given an invalid BIN when getInstallments is called then the validation message does not contain the BIN`() = runTest {
        // Given
        val core = createCore(FakeApiClient(MPResult.Success(emptyList<InstallmentDto>())))

        // When
        val result = core.getInstallments(bin = "4567AB", amount = 100.0)

        // Then
        val error = assertValidationError(result)
        assertFalse(error.message.contains("4567AB"))
    }

    private fun assertValidationError(result: MPResult<*, MPResultError>): MPResultError.Validation =
        assertIs<MPResultError.Validation>(assertIs<MPResult.Failure<*>>(result).error)

    private fun createCore(apiClient: FakeApiClient): MercadoPagoCore =
        MercadoPagoCore(apiClient = apiClient, config = CoreConfig(productId = "product"))

    private companion object {
        const val MAXIMUM_AMOUNT = 10_000_000_000_000.0
    }
}
