package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertTrue

internal class PlatformTest {

    @Test
    fun `given the build when platformName is called then it returns a non-blank name`() {
        // Given

        // When
        val name = platformName()

        // Then
        assertTrue(name.isNotBlank())
    }
}
