package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.core.FakeApiClient
import com.mercadopago.sdk.core.coremethods.data.remote.dto.IdentificationTypeDto
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CoreMethodsTest {

    @Test
    fun `given a successful response when getIdentificationTypes is called then it returns the types`() = runTest {
        // Given
        val dto = IdentificationTypeDto(id = "DNI", name = "DNI", type = "number", minLength = 7, maxLength = 8)
        val coreMethods = createCoreMethods(FakeApiClient(MPResult.Success(listOf(dto))))

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
        val coreMethods = createCoreMethods(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = coreMethods.getIdentificationTypes()

        // Then
        assertEquals(MPResult.Failure(error), result)
    }
}
