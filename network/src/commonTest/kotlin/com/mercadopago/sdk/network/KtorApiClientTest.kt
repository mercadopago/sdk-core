package com.mercadopago.sdk.network

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.common.model.NetworkErrorType
import com.mercadopago.sdk.network.api.ApiBody
import com.mercadopago.sdk.network.api.ApiMethod
import com.mercadopago.sdk.network.api.ApiRequest
import com.mercadopago.sdk.network.client.createHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

internal class KtorApiClientTest {

    private fun clientFor(engine: MockEngine) = KtorApiClient(createHttpClient(engine))

    private fun getRequest() =
        ApiRequest(
            method = ApiMethod.GET,
            path = "payments",
            responseDeserializer = JsonObject.serializer(),
        )

    @Test
    fun `given a success response with a json body when executed then the decoded value is returned`() = runTest {
        // Given
        val engine = MockEngine {
            respond("""{"id":"1234"}""", HttpStatusCode.OK, headersOf("Content-Type", "application/json"))
        }

        // When
        val result = clientFor(engine).execute(getRequest())

        // Then
        assertIs<MPResult.Success<JsonObject>>(result)
        assertEquals("1234", result.data["id"]?.jsonPrimitive?.content)
    }

    @Test
    fun `given an error response with a json body when executed then the api error keeps the status and fields`() = runTest {
        // Given
        val engine = MockEngine {
            respond(
                """{"message":"invalid","code":"400","error_code":"EMPTY","user_error_message":"No methods"}""",
                HttpStatusCode.BadRequest,
                headersOf("Content-Type", "application/json"),
            )
        }

        // When
        val result = clientFor(engine).execute(getRequest())

        // Then
        assertEquals(
            MPResult.Failure(
                MPResultError.Api(
                    httpStatus = 400,
                    code = "400",
                    message = "invalid",
                    errorCode = "EMPTY",
                    userErrorMessage = "No methods",
                ),
            ),
            result,
        )
    }

    @Test
    fun `given an error response without a json body when executed then the status is kept`() = runTest {
        // Given
        val engine = MockEngine { respond("<html>error</html>", HttpStatusCode.InternalServerError) }

        // When
        val result = clientFor(engine).execute(getRequest())

        // Then
        assertEquals(
            MPResult.Failure(MPResultError.Api(httpStatus = 500, code = "UNKNOWN_ERROR", message = "UNKNOWN_ERROR")),
            result,
        )
    }

    @Test
    fun `given a success response with an empty body when executed then an empty body invalid response is returned`() = runTest {
        // Given
        val engine = MockEngine { respond("", HttpStatusCode.OK) }

        // When
        val result = clientFor(engine).execute(getRequest())

        // Then
        assertEquals(
            MPResult.Failure(MPResultError.InvalidResponse(httpStatus = 200, reason = "EMPTY_BODY")),
            result,
        )
    }

    @Test
    fun `given a transport failure when executed then a network error is returned`() = runTest {
        // Given
        val engine = MockEngine { throw IOException("https://api.mercadopago.com/payments?public_key=secret") }

        // When
        val result = clientFor(engine).execute(getRequest())

        // Then
        assertEquals(MPResult.Failure(MPResultError.Network(networkErrorType = NetworkErrorType.UNKNOWN)), result)
    }

    @Test
    fun `given null and empty query and header values when executed then they are not sent`() = runTest {
        // Given
        var sentRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            sentRequest = request
            respond("{}", HttpStatusCode.OK, headersOf("Content-Type", "application/json"))
        }
        val request = ApiRequest(
            method = ApiMethod.GET,
            path = "payments",
            responseDeserializer = JsonObject.serializer(),
            queryParameters = mapOf("bin" to "123456", "site" to null, "empty" to ""),
            headers = mapOf("X-Product-id" to "product", "X-Null" to null),
        )

        // When
        clientFor(engine).execute(request)

        // Then
        val sent = sentRequest!!
        assertEquals("123456", sent.url.parameters["bin"])
        assertNull(sent.url.parameters["site"])
        assertNull(sent.url.parameters["empty"])
        assertEquals("product", sent.headers["X-Product-id"])
        assertNull(sent.headers["X-Null"])
    }

    @Test
    fun `given a request with a body when executed then the body is sent as json`() = runTest {
        // Given
        var sentBody: String? = null
        var sentContentType: ContentType? = null
        val engine = MockEngine { request ->
            val body = request.body as TextContent
            sentBody = body.text
            sentContentType = body.contentType
            respond("{}", HttpStatusCode.OK, headersOf("Content-Type", "application/json"))
        }
        val request = ApiRequest(
            method = ApiMethod.POST,
            path = "card_tokens",
            responseDeserializer = JsonObject.serializer(),
            body = ApiBody(
                value = JsonObject(mapOf("bin" to JsonPrimitive("123456"))),
                serializer = JsonObject.serializer(),
            ),
        )

        // When
        clientFor(engine).execute(request)

        // Then
        assertEquals("""{"bin":"123456"}""", sentBody)
        assertEquals(ContentType.Application.Json, sentContentType?.withoutParameters())
    }
}
