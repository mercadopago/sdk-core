package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.core.FakeApiClient
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardIssuerDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardIssuer
import com.mercadopago.sdk.network.api.ApiMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CardIssuersRepositoryTest {

    private val dto = CardIssuerDto(
        id = "24",
        name = "Banco Galicia",
        merchantAccountId = "123",
        processingMode = "aggregator",
        status = "active",
        thumbnail = "https://example.com/galicia.png",
    )

    @Test
    fun `given a successful response when getCardIssuers is called then it returns the mapped issuers`() = runTest {
        // Given
        val repository = createCardIssuersRepository(FakeApiClient(MPResult.Success(listOf(dto))))

        // When
        val result = repository.getCardIssuers(productId = "product", bin = "45678901", paymentMethodId = "visa")

        // Then
        val expected = CardIssuer(
            id = "24",
            name = "Banco Galicia",
            merchantAccountId = "123",
            processingMode = "aggregator",
            status = "active",
            thumbnail = "https://example.com/galicia.png",
        )
        assertEquals(MPResult.Success(listOf(expected)), result)
    }

    @Test
    fun `given a call when getCardIssuers is called then it requests GET on the card issuers path`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<CardIssuerDto>()))
        val repository = createCardIssuersRepository(apiClient)

        // When
        repository.getCardIssuers(productId = "product", bin = "45678901", paymentMethodId = "visa")

        // Then
        val request = apiClient.requests.single()
        assertEquals(ApiMethod.GET, request.method)
        assertEquals("cho-off/v1/card_issuers", request.path)
    }

    @Test
    fun `given an API error when getCardIssuers is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Api(httpStatus = 404, code = "not_found", message = "not found")
        val repository = createCardIssuersRepository(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = repository.getCardIssuers(productId = "product", bin = "45678901", paymentMethodId = "visa")

        // Then
        assertEquals(MPResult.Failure(error), result)
    }
}
