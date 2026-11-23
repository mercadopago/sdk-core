package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.AgreementDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.InstallmentIssuerDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.MerchantAccountDto
import com.mercadopago.sdk.core.coremethods.data.remote.dto.TimeFrameDto
import com.mercadopago.sdk.core.coremethods.domain.model.Agreement
import com.mercadopago.sdk.core.coremethods.domain.model.Installment
import com.mercadopago.sdk.core.coremethods.domain.model.InstallmentIssuer
import com.mercadopago.sdk.core.coremethods.domain.model.MerchantAccount
import com.mercadopago.sdk.core.coremethods.domain.model.TimeFrame
import kotlin.test.Test
import kotlin.test.assertEquals

internal class InstallmentMapperTest {

    @Test
    fun `given an installment with agreements when mapped then it keeps every field of the agreements`() {
        // Given
        val dto = InstallmentDto(
            paymentMethodId = "visa",
            paymentTypeId = "credit_card",
            issuer = InstallmentIssuerDto(id = "24", thumbnail = "https://example.com/issuer.png"),
            processingMode = "aggregator",
            payerCosts = emptyList(),
            thumbnail = "https://example.com/visa.png",
            merchantAccountId = "merchant",
            agreements = listOf(
                AgreementDto(
                    timeFrame = TimeFrameDto(startDate = "2026-01-01T00:00:00Z", endDate = "2026-12-31T23:59:59Z"),
                    merchantAccounts = listOf(MerchantAccountDto(id = "account", paymentMethodOptionId = "option")),
                ),
            ),
        )

        // When
        val installment = dto.toDomain()

        // Then
        val expected = Installment(
            paymentMethodId = "visa",
            paymentTypeId = "credit_card",
            thumbnail = "https://example.com/visa.png",
            issuer = InstallmentIssuer(id = "24", thumbnail = "https://example.com/issuer.png"),
            processingMode = "aggregator",
            merchantAccountId = "merchant",
            payerCosts = emptyList(),
            agreements = listOf(
                Agreement(
                    merchantAccounts = listOf(MerchantAccount(id = "account", paymentMethodOptionId = "option")),
                    timeFrame = TimeFrame(startDate = "2026-01-01T00:00:00Z", endDate = "2026-12-31T23:59:59Z"),
                ),
            ),
        )
        assertEquals(expected, installment)
    }
}
