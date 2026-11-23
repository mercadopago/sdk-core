package com.mercadopago.sdk.core.coremethods.domain.usecase

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.CalendarMonth
import com.mercadopago.sdk.core.DeviceClock
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolder
import com.mercadopago.sdk.core.coremethods.domain.model.CardHolderIdentification
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.core.coremethods.domain.repository.CardTokensRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class CreateCardTokenUseCaseTest {

    private val repositoryResult = MPResult.Failure(MPResultError.Validation(message = "from repository"))

    @Test
    fun `given an invalid card number when createNewCard is called then it returns the card number error without calling the repository`() = runTest {
        // Given
        val repository = FakeCardTokensRepository(repositoryResult)
        val useCase = createUseCase(repository)

        // When
        val result = useCase.createNewCard(
            productId = "product",
            sessionId = "session",
            cardData = createCardData(cardNumber = "4509953566233705"),
            securityCodeLength = null,
        )

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The card number is invalid")), result)
        assertTrue(repository.cardDataCalls.isEmpty())
    }

    @Test
    fun `given an expired card when createNewCard is called then it returns the expired error without calling the repository`() = runTest {
        // Given
        val repository = FakeCardTokensRepository(repositoryResult)
        val useCase = createUseCase(repository, currentMonth = CalendarMonth(year = 2026, month = 11))

        // When
        val result = useCase.createNewCard(
            productId = "product",
            sessionId = "session",
            cardData = createCardData(expirationDate = "10/26"),
            securityCodeLength = null,
        )

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The card is expired")), result)
        assertTrue(repository.cardDataCalls.isEmpty())
    }

    @Test
    fun `given a valid new card when createNewCard is called then it sends it to the repository with the product and session`() = runTest {
        // Given
        val repository = FakeCardTokensRepository(repositoryResult)
        val useCase = createUseCase(repository)

        // When
        val result = useCase.createNewCard(
            productId = "product",
            sessionId = "session",
            cardData = createCardData(),
            securityCodeLength = null,
        )

        // Then
        assertEquals(repositoryResult, result)
        assertEquals(listOf("product" to "session"), repository.cardDataCalls)
    }

    @Test
    fun `given a saved card with an invalid security code when createSavedCard is called then it returns the security code error without calling the repository`() = runTest {
        // Given
        val repository = FakeCardTokensRepository(repositoryResult)
        val useCase = createUseCase(repository)

        // When
        val result = useCase.createSavedCard(
            productId = "product",
            sessionId = "session",
            savedCardData = SavedCardData(cardId = "card-1", securityCode = "98"),
            securityCodeLength = null,
        )

        // Then
        assertEquals(MPResult.Failure(MPResultError.Validation(message = "The security code is invalid")), result)
        assertTrue(repository.savedCardCalls.isEmpty())
    }

    @Test
    fun `given a valid saved card when createSavedCard is called then it sends it to the repository with the product and session`() = runTest {
        // Given
        val repository = FakeCardTokensRepository(repositoryResult)
        val useCase = createUseCase(repository)

        // When
        val result = useCase.createSavedCard(
            productId = "product",
            sessionId = "session",
            savedCardData = SavedCardData(cardId = "card-1", securityCode = "123"),
            securityCodeLength = null,
        )

        // Then
        assertEquals(repositoryResult, result)
        assertEquals(listOf("product" to "session"), repository.savedCardCalls)
    }

    private fun createUseCase(
        repository: FakeCardTokensRepository,
        currentMonth: CalendarMonth = CalendarMonth(year = 2026, month = 11),
    ): CreateCardTokenUseCase =
        CreateCardTokenUseCase(
            cardTokensRepository = repository,
            deviceClock = DeviceClock { currentMonth },
        )

    private fun createCardData(
        cardNumber: String = "4509953566233704",
        expirationDate: String = "11/30",
        securityCode: String = "123",
    ): CardData =
        CardData(
            cardNumber = cardNumber,
            expirationDate = expirationDate,
            securityCode = securityCode,
            cardHolder = CardHolder(
                name = "APRO",
                identification = CardHolderIdentification(type = "CPF", number = "12345678909"),
            ),
        )

    private class FakeCardTokensRepository(
        private val result: MPResult<CardToken, MPResultError>,
    ) : CardTokensRepository {
        val cardDataCalls = mutableListOf<Pair<String, String>>()
        val savedCardCalls = mutableListOf<Pair<String, String>>()

        override suspend fun createCardToken(
            productId: String,
            sessionId: String,
            cardData: CardData,
        ): MPResult<CardToken, MPResultError> {
            cardDataCalls += productId to sessionId
            return result
        }

        override suspend fun createCardToken(
            productId: String,
            sessionId: String,
            savedCardData: SavedCardData,
        ): MPResult<CardToken, MPResultError> {
            savedCardCalls += productId to sessionId
            return result
        }
    }
}
