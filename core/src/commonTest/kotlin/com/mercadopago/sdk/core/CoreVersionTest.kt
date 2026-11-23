package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertTrue

internal class CoreVersionTest {

    @Test
    fun `given the build when CORE_VERSION is read then it is not blank`() {
        // Given

        // When
        val coreVersion = CORE_VERSION

        // Then
        assertTrue(coreVersion.isNotBlank())
    }
}
