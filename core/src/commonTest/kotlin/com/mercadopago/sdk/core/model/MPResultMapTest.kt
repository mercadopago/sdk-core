package com.mercadopago.sdk.core.model

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MPResultMapTest {

    @Test
    fun `given a success when map is called then the data is transformed`() {
        // Given
        val result: MPResult<Int, MPResultError> = MPResult.Success(2)

        // When
        val mapped = result.map { it * 10 }

        // Then
        assertEquals(MPResult.Success(20), mapped)
    }

    @Test
    fun `given a failure when map is called then the same failure is returned`() {
        // Given
        val error = MPResultError.Network(NetworkErrorType.NO_CONNECTION)
        val result: MPResult<Int, MPResultError> = MPResult.Failure(error)

        // When
        val mapped = result.map { it * 10 }

        // Then
        assertEquals(MPResult.Failure(error), mapped)
    }
}
