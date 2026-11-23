package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.PayerCostDto
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PayerCostMapperTest {

    @Test
    fun `given null labels and rate collector when mapped then both lists are empty`() {
        // Given
        val dto = PayerCostDto(
            installments = 1,
            installmentAmount = 100.0,
            installmentRate = 0.0,
            totalAmount = 100.0,
            minAllowedAmount = 0.0,
            maxAllowedAmount = 1000.0,
            installmentRateCollector = null,
            labels = null,
        )

        // When
        val payerCost = dto.toDomain()

        // Then
        assertEquals(emptyList(), payerCost.labels)
        assertEquals(emptyList(), payerCost.installmentRateCollector)
    }
}
