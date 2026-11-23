package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.PaymentMethodDto
import com.mercadopago.sdk.core.coremethods.domain.model.PaymentMethod
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PaymentMethodMapperTest {

    @Test
    fun `given a DTO without card and thumbnail when toDomain is called then those fields are null`() {
        // Given
        val dto = PaymentMethodDto(
            id = "pix",
            paymentTypeId = "bank_transfer",
            status = "active",
            processingMode = "aggregator",
            merchantAccountId = "123",
            siteId = "MLA",
        )

        // When
        val model = dto.toDomain()

        // Then
        val expected = PaymentMethod(
            id = "pix",
            paymentTypeId = "bank_transfer",
            status = "active",
            processingMode = "aggregator",
            merchantAccountId = "123",
            siteId = "MLA",
            thumbnail = null,
            card = null,
        )
        assertEquals(expected, model)
    }

    @Test
    fun `given a DTO with thumbnail when toDomain is called then it is mapped`() {
        // Given
        val dto = PaymentMethodDto(
            id = "master",
            paymentTypeId = "credit_card",
            status = "active",
            processingMode = "aggregator",
            merchantAccountId = "456",
            siteId = "MLA",
            thumbnail = "https://example.com/master.png",
        )

        // When
        val model = dto.toDomain()

        // Then
        assertEquals("https://example.com/master.png", model.thumbnail)
        assertEquals(null, model.card)
    }
}
