package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
internal class CardIssuerDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private val serializer = ListSerializer(CardIssuerDto.serializer())

    @Test
    fun `given a snake_case payload when decoded then it returns the card issuers`() {
        // Given
        val payload = """
            [{"id":"24","name":"Banco Galicia","merchant_account_id":"123","processing_mode":"aggregator",
            "status":"active","thumbnail":"https://example.com/galicia.png","extra":"ignored"}]
        """.trimIndent()

        // When
        val issuers = json.decodeFromString(serializer, payload)

        // Then
        val expected = CardIssuerDto(
            id = "24",
            name = "Banco Galicia",
            merchantAccountId = "123",
            processingMode = "aggregator",
            status = "active",
            thumbnail = "https://example.com/galicia.png",
        )
        assertEquals(listOf(expected), issuers)
    }
}
