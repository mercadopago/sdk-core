package com.mercadopago.sdk.core

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardIssuerDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class MercadoPagoCoreCardIssuersTest {

    @Test
    fun `given a BIN with letters when getCardIssuers is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<CardIssuerDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getCardIssuers(bin = "4567AB", paymentMethodId = "visa")

        // Then
        assertIs<MPResult.Failure<MPResultError.Validation>>(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a blank payment method id when getCardIssuers is called then it returns a validation error without calling the API`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<CardIssuerDto>()))
        val core = createCore(apiClient)

        // When
        val result = core.getCardIssuers(bin = "45678901", paymentMethodId = " ")

        // Then
        assertIs<MPResult.Failure<MPResultError.Validation>>(result)
        assertTrue(apiClient.requests.isEmpty())
    }

    @Test
    fun `given a valid BIN and payment method when getCardIssuers is called then it calls the API once`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<CardIssuerDto>()))
        val core = createCore(apiClient)

        // When
        core.getCardIssuers(bin = "45678901", paymentMethodId = "visa")

        // Then
        assertTrue(apiClient.requests.size == 1)
    }

    @Test
    fun `given an invalid BIN when getCardIssuers is called then the validation message does not contain the BIN`() = runTest {
        // Given
        val core = createCore(FakeApiClient(MPResult.Success(emptyList<CardIssuerDto>())))

        // When
        val result = core.getCardIssuers(bin = "4567AB", paymentMethodId = "visa")

        // Then
        val error = assertIs<MPResult.Failure<MPResultError.Validation>>(result).error
        assertFalse(error.message.contains("4567AB"))
    }

    private fun createCore(apiClient: FakeApiClient): MercadoPagoCore =
        MercadoPagoCore(apiClient = apiClient, config = CoreConfig(productId = "product"))
}
