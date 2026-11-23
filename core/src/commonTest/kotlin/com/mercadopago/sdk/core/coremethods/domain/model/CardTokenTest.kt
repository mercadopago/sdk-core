package com.mercadopago.sdk.core.coremethods.domain.model

import com.mercadopago.sdk.common.model.MPResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class CardTokenTest {

    private val cardTokenFixture = CardToken(token = "tok-fixture-0001", lastFourDigits = "3704")

    @Test
    fun `given a card token when converted to text then neither the token nor the last four digits appear`() {
        // Given
        val cardToken = cardTokenFixture

        // When
        val text = listOf(cardToken.toString(), listOf(cardToken).toString()).joinToString()

        // Then
        assertEquals("CardToken(***)", cardToken.toString())
        assertEquals("[CardToken(***)]", listOf(cardToken).toString())
        assertFalse(text.contains("tok-fixture-0001"))
        assertFalse(text.contains("3704"))
    }

    @Test
    fun `given two card tokens with the same values when compared then they are not equal`() {
        // Given
        val first = CardToken(token = "tok-fixture-0001", lastFourDigits = "3704")
        val second = CardToken(token = "tok-fixture-0001", lastFourDigits = "3704")

        // When
        val equal = first == second

        // Then
        assertFalse(equal)
    }

    @Test
    fun `given a success result with a card token when converted to text then the token does not appear`() {
        // Given
        val result: MPResult<CardToken, Nothing> = MPResult.Success(cardTokenFixture)

        // When
        val text = result.toString()

        // Then
        assertFalse(text.contains("tok-fixture-0001"))
        assertFalse(text.contains("3704"))
    }

    @Test
    fun `given a card token when its values are read then they are the ones it was created with`() {
        // Given
        val cardToken = cardTokenFixture

        // When
        val values = cardToken.token to cardToken.lastFourDigits

        // Then
        assertEquals("tok-fixture-0001" to "3704", values)
    }
}
