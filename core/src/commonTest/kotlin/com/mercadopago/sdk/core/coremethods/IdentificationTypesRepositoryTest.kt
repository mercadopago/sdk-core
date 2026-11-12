package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.core.FakeApiClient
import com.mercadopago.sdk.core.coremethods.data.remote.dto.IdentificationTypeDto
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import com.mercadopago.sdk.network.api.ApiMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class IdentificationTypesRepositoryTest {

    @Test
    fun `given a successful response when getIdentificationTypes is called then it returns the types`() = runTest {
        // Given
        val dto = IdentificationTypeDto(id = "CPF", name = "CPF", type = "number", minLength = 11, maxLength = 11)
        val repository = createIdentificationTypesRepository(FakeApiClient(MPResult.Success(listOf(dto))))

        // When
        val result = repository.getIdentificationTypes()

        // Then
        val expected = IdentificationType(id = "CPF", name = "CPF", type = "number", minLength = 11, maxLength = 11)
        assertEquals(MPResult.Success(listOf(expected)), result)
    }

    @Test
    fun `given a call when getIdentificationTypes is called then it requests GET on the types path`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<IdentificationTypeDto>()))
        val repository = createIdentificationTypesRepository(apiClient)

        // When
        repository.getIdentificationTypes()

        // Then
        val request = apiClient.requests.single()
        assertEquals(ApiMethod.GET, request.method)
        assertEquals("cho-off/v1/identification_types", request.path)
    }

    @Test
    fun `given an API error when getIdentificationTypes is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Api(httpStatus = 400, code = "bad_request", message = "Invalid public key")
        val repository = createIdentificationTypesRepository(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = repository.getIdentificationTypes()

        // Then
        assertEquals(MPResult.Failure(error), result)
    }

    @Test
    fun `given a network error when getIdentificationTypes is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Network(NetworkErrorType.TIMEOUT)
        val repository = createIdentificationTypesRepository(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = repository.getIdentificationTypes()

        // Then
        assertEquals(MPResult.Failure(error), result)
    }

    @Test
    fun `given an invalid response when getIdentificationTypes is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.InvalidResponse(httpStatus = 200, reason = "EMPTY_BODY")
        val repository = createIdentificationTypesRepository(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = repository.getIdentificationTypes()

        // Then
        assertEquals(MPResult.Failure(error), result)
    }
}
