package com.mercadopago.sdk.core.coremethods.data.remote.mapper

import com.mercadopago.sdk.core.coremethods.data.remote.dto.IdentificationTypeDto
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import kotlin.test.Test
import kotlin.test.assertEquals

internal class IdentificationTypeMapperTest {

    @Test
    fun `given a DTO when toDomain is called then every field is mapped`() {
        // Given
        val dto = IdentificationTypeDto(id = "DNI", name = "DNI", type = "number", minLength = 7, maxLength = 8)

        // When
        val model = dto.toDomain()

        // Then
        assertEquals(IdentificationType(id = "DNI", name = "DNI", type = "number", minLength = 7, maxLength = 8), model)
    }

    @Test
    fun `given a DTO without type when toDomain is called then type is null`() {
        // Given
        val dto = IdentificationTypeDto(id = "Otro", name = "Otro", type = null, minLength = 3, maxLength = 10)

        // When
        val model = dto.toDomain()

        // Then
        assertEquals(IdentificationType(id = "Otro", name = "Otro", type = null, minLength = 3, maxLength = 10), model)
    }
}
