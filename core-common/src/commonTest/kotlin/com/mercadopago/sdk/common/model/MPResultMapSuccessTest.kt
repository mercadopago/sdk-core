package com.mercadopago.sdk.common.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class MPResultMapSuccessTest {

    @Test
    fun `given a success when mapSuccess is called then the data is transformed`() {
        // Given
        val result: MPResult<Int, MPResultError> = MPResult.Success(2)

        // When
        val mapped = result.mapSuccess { it * 10 }

        // Then
        assertEquals(MPResult.Success(20), mapped)
    }

    @Test
    fun `given a failure when mapSuccess is called then the same failure is returned`() {
        // Given
        val error = MPResultError.Api(httpStatus = 500, code = "failure", message = "failure")
        val result: MPResult<Int, MPResultError> = MPResult.Failure(error)

        // When
        val mapped = result.mapSuccess { it * 10 }

        // Then
        assertSame(result, mapped)
    }

    @Test
    fun `given a failure when mapSuccess is called then the transform is not called`() {
        // Given
        val result: MPResult<Int, MPResultError> =
            MPResult.Failure(MPResultError.Validation(message = "invalid"))
        var transformCalls = 0

        // When
        result.mapSuccess { transformCalls++ }

        // Then
        assertEquals(0, transformCalls)
    }
}
