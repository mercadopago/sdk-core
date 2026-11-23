package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class CoreVersionJvmTest {

    @Test
    fun `given the build version when CORE_VERSION is read then it is equal to the build version`() {
        // Given
        val buildVersion = System.getProperty("mercadopago.core.version")

        // When
        val coreVersion = CORE_VERSION

        // Then
        assertNotNull(buildVersion)
        assertEquals(buildVersion, coreVersion)
    }
}
