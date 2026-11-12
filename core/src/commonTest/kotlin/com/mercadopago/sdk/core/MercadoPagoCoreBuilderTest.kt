package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class MercadoPagoCoreBuilderTest {

    @Test
    fun `given a blank public key when build is called then it fails`() {
        // Given
        val builder = MercadoPagoCore.Builder(publicKey = " ")

        // When
        val thrown = assertFailsWith<IllegalArgumentException> { builder.build() }

        // Then
        assertEquals("The public key must not be blank", thrown.message)
    }

    @Test
    fun `given a public key when build is called then the core is created and can be closed`() {
        // Given
        val builder = MercadoPagoCore.Builder(publicKey = "TEST-public-key")

        // When
        val core = builder.build()

        // Then
        assertNotNull(core)
        core.close()
    }
}
