package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

internal class MercadoPagoCoreBuilderTest {

    @Test
    fun `given a blank public key when build is called then it fails`() {
        // Given
        val builder = MercadoPagoCore.Builder(publicKey = " ", productId = "product")

        // When
        val thrown = assertFailsWith<IllegalArgumentException> { builder.build() }

        // Then
        assertEquals("The public key must not be blank", thrown.message)
    }

    @Test
    fun `given a blank product id when build is called then it fails`() {
        // Given
        val builder = MercadoPagoCore.Builder(publicKey = "TEST-public-key", productId = " ")

        // When
        val thrown = assertFailsWith<IllegalArgumentException> { builder.build() }

        // Then
        assertEquals("The product id must not be blank", thrown.message)
    }

    @Test
    fun `given a public key when build is called then the core is created and can be closed`() {
        // Given
        val builder = MercadoPagoCore.Builder(publicKey = "TEST-public-key", productId = "product")

        // When
        val core = builder.build()

        // Then
        assertNotNull(core)
        core.close()
    }

    @Test
    fun `given no device info provider when build is called then the core has no device info provider`() {
        // Given
        val builder = MercadoPagoCore.Builder(publicKey = "TEST-public-key", productId = "product")

        // When
        val core = builder.build()

        // Then
        assertNull(core.config.deviceInfoProvider)
        assertNull(readDeviceInfo(core.config.deviceInfoProvider))
        core.close()
    }

    @Test
    fun `given a device info provider when build is called then the core keeps the provider`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { """{"os_version":"14"}""" }
        val builder = MercadoPagoCore.Builder(publicKey = "TEST-public-key", productId = "product")
            .deviceInfoProvider(deviceInfoProvider)

        // When
        val core = builder.build()

        // Then
        assertSame(deviceInfoProvider, core.config.deviceInfoProvider)
        core.close()
    }
}
