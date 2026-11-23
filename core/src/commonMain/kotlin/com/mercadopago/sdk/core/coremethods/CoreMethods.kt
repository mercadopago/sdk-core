package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardIssuer
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import com.mercadopago.sdk.core.coremethods.domain.model.Installment
import com.mercadopago.sdk.core.coremethods.domain.model.PaymentMethod
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.core.coremethods.domain.repository.CardIssuersRepository
import com.mercadopago.sdk.core.coremethods.domain.repository.IdentificationTypesRepository
import com.mercadopago.sdk.core.coremethods.domain.repository.InstallmentsRepository
import com.mercadopago.sdk.core.coremethods.domain.repository.PaymentMethodsRepository
import com.mercadopago.sdk.core.coremethods.domain.usecase.CreateCardTokenUseCase

internal class CoreMethods(
    private val identificationTypesRepository: IdentificationTypesRepository,
    private val paymentMethodsRepository: PaymentMethodsRepository,
    private val cardIssuersRepository: CardIssuersRepository,
    private val installmentsRepository: InstallmentsRepository,
    private val createCardTokenUseCase: CreateCardTokenUseCase,
) {
    suspend fun getIdentificationTypes(): MPResult<List<IdentificationType>, MPResultError> =
        identificationTypesRepository.getIdentificationTypes()

    suspend fun getPaymentMethods(productId: String, bin: String): MPResult<List<PaymentMethod>, MPResultError> =
        paymentMethodsRepository.getPaymentMethods(productId = productId, bin = bin)

    suspend fun getCardIssuers(
        productId: String,
        bin: String,
        paymentMethodId: String,
    ): MPResult<List<CardIssuer>, MPResultError> =
        cardIssuersRepository.getCardIssuers(productId = productId, bin = bin, paymentMethodId = paymentMethodId)

    suspend fun getInstallments(
        productId: String,
        bin: String,
        amount: Double,
    ): MPResult<List<Installment>, MPResultError> =
        installmentsRepository.getInstallments(productId = productId, bin = bin, amount = amount)

    suspend fun createCardToken(
        productId: String,
        sessionId: String,
        cardData: CardData,
        securityCodeLength: Int?,
    ): MPResult<CardToken, MPResultError> =
        createCardTokenUseCase.createNewCard(
            productId = productId,
            sessionId = sessionId,
            cardData = cardData,
            securityCodeLength = securityCodeLength,
        )

    suspend fun createCardToken(
        productId: String,
        sessionId: String,
        savedCardData: SavedCardData,
        securityCodeLength: Int?,
    ): MPResult<CardToken, MPResultError> =
        createCardTokenUseCase.createSavedCard(
            productId = productId,
            sessionId = sessionId,
            savedCardData = savedCardData,
            securityCodeLength = securityCodeLength,
        )
}
