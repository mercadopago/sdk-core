package com.mercadopago.sdk.network.client

import io.ktor.http.URLProtocol
import io.ktor.http.Url
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ApiEndpointTest {

    @Test
    fun `given the api endpoint when parsed then it uses https the allowed host and no credentials`() {
        // Given
        val url = Url(ApiEndpoint.BASE_URL)

        // When
        val protocol = url.protocol
        val host = url.host
        val user = url.user

        // Then
        assertEquals(URLProtocol.HTTPS, protocol)
        assertEquals("api.mercadopago.com", host)
        assertEquals("api.mercadopago.com", ApiEndpoint.HOST)
        assertNull(user)
    }
}
