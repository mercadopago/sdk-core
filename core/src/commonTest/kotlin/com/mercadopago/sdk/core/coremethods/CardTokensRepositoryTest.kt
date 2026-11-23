package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.core.DeviceInfoProvider
import com.mercadopago.sdk.core.FakeApiClient
import com.mercadopago.sdk.core.coremethods.data.remote.dto.CardTokenResponseDto
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolder
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolderIdentification
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.network.api.ApiMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

internal class CardTokensRepositoryTest {

    private val productId = "product-fixture"
    private val sessionId = "123e4567-e89b-42d3-a456-426614174000"
    private val deviceInfoProvider = DeviceInfoProvider { """{"fingerprint_id":"device-fixture"}""" }

    private val cardData = CardData(
        cardNumber = "4509953566233704",
        expirationDate = "11/30",
        securityCode = "123",
        cardHolder = CardHolder(
            name = "APRO",
            identification = CardHolderIdentification(type = "CPF", number = "12345678909"),
        ),
    )
    private val savedCardData = SavedCardData(cardId = "9876543210", securityCode = "123")

    private val cardDataValues = listOf("4509953566233704", "450995", "123", "11/30", "APRO", "12345678909")
    private val savedCardDataValues = listOf("9876543210", "123")

    private val responseDto = CardTokenResponseDto(id = "tok-fixture-0001", lastFourDigits = "3704")

    private val apiErrorWithJson = MPResultError.Api(
        httpStatus = 400,
        code = "bad_request",
        message = "invalid parameters",
        errorCode = "E301",
        userErrorMessage = "Check the card data",
    )
    private val apiErrorWithoutJson = MPResultError.Api(
        httpStatus = 500,
        code = "UNKNOWN_ERROR",
        message = "UNKNOWN_ERROR",
    )
    private val emptyBodyError = MPResultError.InvalidResponse(httpStatus = 201, reason = "EMPTY_BODY")
    private val decodingError = MPResultError.InvalidResponse(httpStatus = 201, reason = "DECODING_FAILED")
    private val networkError = MPResultError.Network(NetworkErrorType.TIMEOUT)

    private val cardDataEchoError = MPResultError.Api(
        httpStatus = 400,
        code = "4509953566233704",
        message = "card 4509953566233704 cvv 123 APRO",
        errorCode = "12345678909",
        userErrorMessage = "APRO 12345678909",
    )
    private val cardDataEchoTextError = MPResultError.Api(
        httpStatus = 400,
        code = "card 4509953566233704",
        message = "cvv 123 APRO",
        errorCode = "doc 12345678909",
        userErrorMessage = "4509953566233704 11/30",
    )
    private val savedCardDataEchoError = MPResultError.Api(
        httpStatus = 400,
        code = "9876543210",
        message = "card 9876543210 cvv 123",
        errorCode = "cvv_123",
        userErrorMessage = "9876543210",
    )

    private val failures = listOf(
        apiErrorWithJson,
        apiErrorWithoutJson,
        cardDataEchoError,
        cardDataEchoTextError,
        savedCardDataEchoError,
        emptyBodyError,
        decodingError,
        networkError,
    )

    private fun safeApiError(httpStatus: Int, code: String = "UNKNOWN_ERROR", errorCode: String? = null) =
        MPResultError.Api(
            httpStatus = httpStatus,
            code = code,
            message = "Card token request failed",
            errorCode = errorCode,
            userErrorMessage = null,
        )

    private fun failingRepository(mpResultError: MPResultError) =
        createCardTokensRepository(FakeApiClient(MPResult.Failure(mpResultError)), deviceInfoProvider)

    @Test
    fun `given a successful response when a new card token is created then it returns the card token`() = runTest {
        // Given
        val repository = createCardTokensRepository(FakeApiClient(MPResult.Success(responseDto)), deviceInfoProvider)

        // When
        val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        val cardToken = assertIs<MPResult.Success<CardToken>>(result).data
        assertEquals("tok-fixture-0001", cardToken.token)
        assertEquals("3704", cardToken.lastFourDigits)
    }

    @Test
    fun `given a successful response when a saved card token is created then it returns the card token`() = runTest {
        // Given
        val repository = createCardTokensRepository(FakeApiClient(MPResult.Success(responseDto)), deviceInfoProvider)

        // When
        val result = repository.createCardToken(
            productId = productId,
            sessionId = sessionId,
            savedCardData = savedCardData,
        )

        // Then
        val cardToken = assertIs<MPResult.Success<CardToken>>(result).data
        assertEquals("tok-fixture-0001", cardToken.token)
        assertEquals("3704", cardToken.lastFourDigits)
    }

    @Test
    fun `given a response without last four digits when a card token is created then they are empty`() = runTest {
        // Given
        val dto = CardTokenResponseDto(id = "tok-fixture-0001")
        val repository = createCardTokensRepository(FakeApiClient(MPResult.Success(dto)), deviceInfoProvider = null)

        // When
        val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        assertEquals("", assertIs<MPResult.Success<CardToken>>(result).data.lastFourDigits)
    }

    @Test
    fun `given both variants when a card token is created then it requests POST on the card tokens path`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(responseDto))
        val repository = createCardTokensRepository(apiClient, deviceInfoProvider)

        // When
        repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)
        repository.createCardToken(productId = productId, sessionId = sessionId, savedCardData = savedCardData)

        // Then
        assertEquals(2, apiClient.requests.size)
        apiClient.requests.forEach { request ->
            assertEquals(ApiMethod.POST, request.method)
            assertEquals("v1/card_tokens", request.path)
        }
    }

    @Test
    fun `given an error with JSON when a card token is created then it returns the safe API error`() = runTest {
        // Given
        val repository = failingRepository(apiErrorWithJson)

        // When
        val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        assertEquals(MPResult.Failure(safeApiError(httpStatus = 400, code = "bad_request", errorCode = "E301")), result)
    }

    @Test
    fun `given an error with JSON when a saved card token is created then it returns the safe API error`() = runTest {
        // Given
        val repository = failingRepository(apiErrorWithJson)

        // When
        val result = repository.createCardToken(
            productId = productId,
            sessionId = sessionId,
            savedCardData = savedCardData,
        )

        // Then
        assertEquals(MPResult.Failure(safeApiError(httpStatus = 400, code = "bad_request", errorCode = "E301")), result)
    }

    @Test
    fun `given an error without JSON when a card token is created then it returns the unknown API error`() = runTest {
        // Given
        val repository = failingRepository(apiErrorWithoutJson)

        // When
        val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        assertEquals(MPResult.Failure(safeApiError(httpStatus = 500)), result)
    }

    @Test
    fun `given codes that are not identifiers when a card token is created then they become unknown`() = runTest {
        // Given
        val codes = listOf("bad request", "a".repeat(65), "bad/request", "")

        // When
        val results = codes.map { code ->
            val error = MPResultError.Api(httpStatus = 400, code = code, message = "message", errorCode = code)
            failingRepository(error).createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)
        }

        // Then
        results.forEach { result -> assertEquals(MPResult.Failure(safeApiError(httpStatus = 400)), result) }
    }

    @Test
    fun `given codes with 64 characters when a card token is created then they are kept`() = runTest {
        // Given
        val code = "a_b.c-D9".repeat(8)
        val error = MPResultError.Api(httpStatus = 422, code = code, message = "message", errorCode = code)
        val repository = failingRepository(error)

        // When
        val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        assertEquals(MPResult.Failure(safeApiError(httpStatus = 422, code = code, errorCode = code)), result)
    }

    @Test
    fun `given an error echoing the card data when a card token is created then no sentinel appears`() = runTest {
        // Given
        val repository = failingRepository(cardDataEchoError)

        // When
        val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        assertEquals(MPResult.Failure(safeApiError(httpStatus = 400)), result)
        val error = assertIs<MPResult.Failure<MPResultError>>(result).error
        val text = error.toString()
        cardDataValues.forEach { value -> assertFalse(text.contains(value), value) }
    }

    @Test
    fun `given identifier codes containing a card value when a card token is created then they become unknown`() =
        runTest {
            // Given
            val codes = listOf("4509953566233704", "cvv_123", "apro", "doc-12345678909")

            // When
            val results = codes.map { code ->
                val error = MPResultError.Api(httpStatus = 400, code = code, message = "message", errorCode = code)
                failingRepository(error).createCardToken(
                    productId = productId,
                    sessionId = sessionId,
                    cardData = cardData,
                )
            }

            // Then
            results.forEach { result -> assertEquals(MPResult.Failure(safeApiError(httpStatus = 400)), result) }
        }

    @Test
    fun `given an error echoing the saved card data when a card token is created then no sentinel appears`() =
        runTest {
            // Given
            val repository = failingRepository(savedCardDataEchoError)

            // When
            val result = repository.createCardToken(
                productId = productId,
                sessionId = sessionId,
                savedCardData = savedCardData,
            )

            // Then
            assertEquals(MPResult.Failure(safeApiError(httpStatus = 400)), result)
            val text = result.toString()
            savedCardDataValues.forEach { value -> assertFalse(text.contains(value), value) }
        }

    @Test
    fun `given an empty body when a card token is created then it returns the empty body error`() = runTest {
        // Given
        val repository = failingRepository(emptyBodyError)

        // When
        val result = repository.createCardToken(
            productId = productId,
            sessionId = sessionId,
            savedCardData = savedCardData,
        )

        // Then
        assertEquals(MPResult.Failure(emptyBodyError), result)
    }

    @Test
    fun `given a decoding failure when a card token is created then it returns the same invalid response error`() =
        runTest {
            // Given
            val repository = failingRepository(decodingError)

            // When
            val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

            // Then
            assertEquals(MPResult.Failure(decodingError), result)
        }

    @Test
    fun `given a transport failure when a card token is created then it returns the network error`() = runTest {
        // Given
        val repository = failingRepository(networkError)

        // When
        val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        assertEquals(MPResult.Failure(networkError), result)
    }

    @Test
    fun `given a failure when a card token is created then it does not retry the request`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Failure(networkError))
        val repository = createCardTokensRepository(apiClient, deviceInfoProvider)

        // When
        repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        assertEquals(1, apiClient.requests.size)
    }

    @Test
    fun `given each failure when a card token is created then no card data appears in the error text`() = runTest {
        // Given
        val newCardTexts = mutableListOf<String>()
        val savedCardTexts = mutableListOf<String>()

        // When
        failures.forEach { error ->
            val apiClient = FakeApiClient(MPResult.Failure(error))
            val repository = createCardTokensRepository(apiClient, deviceInfoProvider)
            val newCardResult = repository.createCardToken(
                productId = productId,
                sessionId = sessionId,
                cardData = cardData,
            )
            val savedCardResult = repository.createCardToken(
                productId = productId,
                sessionId = sessionId,
                savedCardData = savedCardData,
            )
            newCardTexts += newCardResult.toString()
            savedCardTexts += savedCardResult.toString()
        }

        // Then
        val newCardText = newCardTexts.joinToString()
        val savedCardText = savedCardTexts.joinToString()
        cardDataValues.forEach { value -> assertFalse(newCardText.contains(value), value) }
        savedCardDataValues.forEach { value -> assertFalse(savedCardText.contains(value), value) }
    }

    @Test
    fun `given a successful response when converted to text then neither the token nor the digits appear`() = runTest {
        // Given
        val repository = createCardTokensRepository(FakeApiClient(MPResult.Success(responseDto)), deviceInfoProvider)

        // When
        val result = repository.createCardToken(productId = productId, sessionId = sessionId, cardData = cardData)

        // Then
        val text = result.toString()
        assertFalse(text.contains("tok-fixture-0001"))
        assertFalse(text.contains("3704"))
    }
}
