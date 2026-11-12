package com.mercadopago.sdk.common.model

import kotlin.test.Test
import kotlin.test.assertEquals

internal class MPResultErrorTest {

    @Test
    fun `given an api error when mapSuccess is called then the http status and code are kept`() {
        // Given
        val error = MPResultError.Api(httpStatus = 422, code = "invalid_card", message = "card rejected")
        val result: MPResult<Int, MPResultError> = MPResult.Failure(error)

        // When
        val mapped = result.mapSuccess { it }

        // Then
        assertEquals(MPResult.Failure(error), mapped)
    }

    @Test
    fun `given an api error with error code and user message when mapSuccess is called then both are kept`() {
        // Given
        val error = MPResultError.Api(
            httpStatus = 400,
            code = "400",
            message = "error",
            errorCode = "EMPTY_PAYMENT_METHODS",
            userErrorMessage = "No payment methods",
        )
        val result: MPResult<Int, MPResultError> = MPResult.Failure(error)

        // When
        val mapped = result.mapSuccess { it }

        // Then
        assertEquals(MPResult.Failure(error), mapped)
    }

    @Test
    fun `given a network error when mapSuccess is called then the error kind is kept`() {
        // Given
        val error = MPResultError.Network(networkErrorType = NetworkErrorType.TIMEOUT)
        val result: MPResult<Int, MPResultError> = MPResult.Failure(error)

        // When
        val mapped = result.mapSuccess { it }

        // Then
        assertEquals(MPResult.Failure(error), mapped)
    }

    @Test
    fun `given an invalid response when mapSuccess is called then the http status is kept`() {
        // Given
        val error = MPResultError.InvalidResponse(
            httpStatus = 200,
            reason = "DECODING_FAILED",
            fieldPath = "payment_methods.id",
        )
        val result: MPResult<Int, MPResultError> = MPResult.Failure(error)

        // When
        val mapped = result.mapSuccess { it }

        // Then
        assertEquals(MPResult.Failure(error), mapped)
    }
}
