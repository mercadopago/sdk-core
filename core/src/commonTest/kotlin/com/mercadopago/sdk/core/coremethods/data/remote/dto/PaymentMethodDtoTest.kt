package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSerializationApi::class)
internal class PaymentMethodDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private val serializer = ListSerializer(PaymentMethodDto.serializer())

    @Test
    fun `given a snake_case payload with card when decoded then it returns the payment methods`() {
        // Given
        val payload = """
            [{"id":"visa","payment_type_id":"credit_card","status":"active","processing_mode":"aggregator",
            "merchant_account_id":"123","site_id":"MLA","thumbnail":"https://example.com/visa.png",
            "card":{"bin":411111,"length":{"min":16,"max":16},"validation":"standard",
            "security_code":{"mode":"mandatory","location":"back","length":3}},"extra":"ignored"}]
        """.trimIndent()

        // When
        val methods = json.decodeFromString(serializer, payload)

        // Then
        val expected = PaymentMethodDto(
            id = "visa",
            paymentTypeId = "credit_card",
            status = "active",
            processingMode = "aggregator",
            merchantAccountId = "123",
            siteId = "MLA",
            thumbnail = "https://example.com/visa.png",
            card = CardInfoDto(
                bin = 411111,
                length = CardLengthDto(min = 16, max = 16),
                validation = "standard",
                securityCode = SecurityCodeDto(mode = "mandatory", location = "back", length = 3),
            ),
        )
        assertEquals(listOf(expected), methods)
    }

    @Test
    fun `given a payload without optional fields when decoded then they are null`() {
        // Given
        val payload = """[{"id":"pix","payment_type_id":"bank_transfer","status":"active",""" +
            """"processing_mode":"aggregator","merchant_account_id":"123","site_id":"MLA"}]"""

        // When
        val method = json.decodeFromString(serializer, payload).single()

        // Then
        assertEquals(null, method.thumbnail)
        assertEquals(null, method.card)
    }

    @Test
    fun `given a payload without processing_mode when decoded then it fails naming the field`() {
        // Given
        val payload = """[{"id":"pix","payment_type_id":"bank_transfer","status":"active",""" +
            """"merchant_account_id":"123","site_id":"MLA"}]"""

        // When
        val thrown = assertFailsWith<SerializationException> { json.decodeFromString(serializer, payload) }

        // Then
        assertContains(thrown.message.orEmpty(), "processingMode")
    }
}
