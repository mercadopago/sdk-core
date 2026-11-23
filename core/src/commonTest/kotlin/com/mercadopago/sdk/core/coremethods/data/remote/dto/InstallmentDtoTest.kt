package com.mercadopago.sdk.core.coremethods.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
internal class InstallmentDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private val serializer = ListSerializer(InstallmentDto.serializer())

    @Test
    fun `given a snake_case payload when decoded then it returns the installments`() {
        // Given
        val payload = """
            [{"payment_method_id":"visa","payment_type_id":"credit_card","thumbnail":"https://example.com/visa.png",
            "issuer":{"id":"24","thumbnail":"https://example.com/issuer.png"},"processing_mode":"aggregator",
            "merchant_account_id":"","payer_costs":[{"installments":3,"installment_amount":35.5,"installment_rate":6.5,
            "installment_rate_collector":["MERCADOPAGO"],"total_amount":106.5,"min_allowed_amount":2,
            "max_allowed_amount":1000000,"discount_rate":0,"reimbursement_rate":0,"labels":["CFT_12,50%|TNA_15,00%"],
            "formatted_label":"CFT: 12,50%  TNA: 15,00%","payment_method_option_id":"option"}],
            "agreements":[{"merchant_accounts":[{"id":"account","payment_method_option_id":"option"}],
            "time_frame":{"start_date":"2026-01-01T00:00:00Z","end_date":"2026-12-31T23:59:59Z"}}],"extra":"ignored"}]
        """.trimIndent()

        // When
        val installments = json.decodeFromString(serializer, payload)

        // Then
        val expected = InstallmentDto(
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
                    discountRate = 0.0,
                    reimbursementRate = 0.0,
                    installmentRateCollector = listOf("MERCADOPAGO"),
                    labels = listOf("CFT_12,50%|TNA_15,00%"),
                    formattedLabel = "CFT: 12,50%  TNA: 15,00%",
                    paymentMethodOptionId = "option",
                ),
            ),
            thumbnail = "https://example.com/visa.png",
            merchantAccountId = "",
            agreements = listOf(
                AgreementDto(
                    timeFrame = TimeFrameDto(startDate = "2026-01-01T00:00:00Z", endDate = "2026-12-31T23:59:59Z"),
                    merchantAccounts = listOf(MerchantAccountDto(id = "account", paymentMethodOptionId = "option")),
                ),
            ),
        )
        assertEquals(listOf(expected), installments)
    }

    @Test
    fun `given null labels and rate collector when decoded then both lists are null`() {
        // Given
        val payload = """
            [{"payment_method_id":"visa","payment_type_id":"credit_card","issuer":{"id":"24"},
            "processing_mode":"aggregator","payer_costs":[{"installments":1,"installment_amount":100,
            "installment_rate":0,"installment_rate_collector":null,"total_amount":100,"min_allowed_amount":0,
            "max_allowed_amount":1000,"labels":null}]}]
        """.trimIndent()

        // When
        val payerCost = json.decodeFromString(serializer, payload).single().payerCosts.single()

        // Then
        assertEquals(null, payerCost.labels)
        assertEquals(null, payerCost.installmentRateCollector)
    }
}
