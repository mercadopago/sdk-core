package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.core.FakeApiClient
import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentIssuerDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.PayerCostDto
import com.mercadopago.sdk.core.coremethods.domain.model.Installment
import com.mercadopago.sdk.core.coremethods.domain.model.InstallmentIssuer
import com.mercadopago.sdk.core.coremethods.domain.model.PayerCost
import com.mercadopago.sdk.network.api.ApiMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class InstallmentsRepositoryTest {

    private val dto = InstallmentDto(
        paymentMethodId = "visa",
        paymentTypeId = "credit_card",
        issuer = InstallmentIssuerDto(id = "24", thumbnail = "https://example.com/issuer.png"),
        processingMode = "aggregator",
        payerCosts = listOf(
            PayerCostDto(
                installments = 3,
                installmentAmount = 35.5,
                installmentRate = 6.5,
                totalAmount = 106.5,
                minAllowedAmount = 2.0,
                maxAllowedAmount = 1000000.0,
                installmentRateCollector = listOf("MERCADOPAGO"),
                labels = listOf("CFT_12,50%|TNA_15,00%"),
                formattedLabel = "CFT: 12,50%  TNA: 15,00%",
            ),
        ),
        thumbnail = "https://example.com/visa.png",
    )

    @Test
    fun `given a successful response when getInstallments is called then it returns the mapped installments`() = runTest {
        // Given
        val repository = createInstallmentsRepository(FakeApiClient(MPResult.Success(listOf(dto))))

        // When
        val result = repository.getInstallments(productId = "product", bin = "45678901", amount = 100.0)

        // Then
        val expected = Installment(
            paymentMethodId = "visa",
            paymentTypeId = "credit_card",
            thumbnail = "https://example.com/visa.png",
            issuer = InstallmentIssuer(id = "24", thumbnail = "https://example.com/issuer.png"),
            processingMode = "aggregator",
            merchantAccountId = "",
            payerCosts = listOf(
                PayerCost(
                    installments = 3,
                    installmentAmount = 35.5,
                    installmentRate = 6.5,
                    installmentRateCollector = listOf("MERCADOPAGO"),
                    totalAmount = 106.5,
                    minAllowedAmount = 2.0,
                    maxAllowedAmount = 1000000.0,
                    discountRate = 0.0,
                    reimbursementRate = 0.0,
                    labels = listOf("CFT_12,50%|TNA_15,00%"),
                    formattedLabel = "CFT: 12,50%  TNA: 15,00%",
                    paymentMethodOptionId = "",
                ),
            ),
            agreements = emptyList(),
        )
        assertEquals(MPResult.Success(listOf(expected)), result)
    }

    @Test
    fun `given a call when getInstallments is called then it requests GET on the installments path`() = runTest {
        // Given
        val apiClient = FakeApiClient(MPResult.Success(emptyList<InstallmentDto>()))
        val repository = createInstallmentsRepository(apiClient)

        // When
        repository.getInstallments(productId = "product", bin = "45678901", amount = 100.0)

        // Then
        val request = apiClient.requests.single()
        assertEquals(ApiMethod.GET, request.method)
        assertEquals("cho-off/v1/installments", request.path)
    }

    @Test
    fun `given a not found error when getInstallments is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Api(httpStatus = 404, code = "not_found", message = "Installments not found")
        val repository = createInstallmentsRepository(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = repository.getInstallments(productId = "product", bin = "45678901", amount = 100.0)

        // Then
        assertEquals(MPResult.Failure(error), result)
    }

    @Test
    fun `given a network error when getInstallments is called then it returns the same error`() = runTest {
        // Given
        val error = MPResultError.Network(NetworkErrorType.NO_CONNECTION)
        val repository = createInstallmentsRepository(FakeApiClient(MPResult.Failure(error)))

        // When
        val result = repository.getInstallments(productId = "product", bin = "45678901", amount = 100.0)

        // Then
        assertEquals(MPResult.Failure(error), result)
    }
}
