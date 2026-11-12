package com.mercadopago.sdk.network.response

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.network.client.createHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

internal class HttpResponseResultMapperTest {

    private suspend fun responseFor(status: HttpStatusCode, body: String): HttpResponse {
        val engine = MockEngine {
            respond(body, status, headersOf("Content-Type", "application/json"))
        }
        return createHttpClient(engine).get("payments")
    }

    @Test
    fun `given a success response with a json body when mapped then the decoded value is returned`() = runTest {
        // Given
        val response = responseFor(HttpStatusCode.OK, """{"id":"1234"}""")

        // When
        val result = response.toResult(JsonObject.serializer())

        // Then
        assertIs<MPResult.Success<JsonObject>>(result)
        assertEquals("1234", result.data["id"]?.jsonPrimitive?.content)
    }

    @Test
    fun `given an error response with a json body when mapped then the api error keeps the status and fields`() = runTest {
        // Given
        val response = responseFor(
            HttpStatusCode.BadRequest,
            """{"message":"invalid request","code":"400","error_code":"EMPTY_PAYMENT_METHODS","user_error_message":"No payment methods"}""",
        )

        // When
        val result = response.toResult(JsonObject.serializer())

        // Then
        assertEquals(
            MPResult.Failure(
                MPResultError.Api(
                    httpStatus = 400,
                    code = "400",
                    message = "invalid request",
                    errorCode = "EMPTY_PAYMENT_METHODS",
                    userErrorMessage = "No payment methods",
                ),
            ),
            result,
        )
    }

    @Test
    fun `given an error response without a json body when mapped then the status is kept and the fields are unknown`() = runTest {
        // Given
        val response = responseFor(HttpStatusCode.NotFound, "<html>not found</html>")

        // When
        val result = response.toResult(JsonObject.serializer())

        // Then
        assertEquals(
            MPResult.Failure(
                MPResultError.Api(
                    httpStatus = 404,
                    code = "UNKNOWN_ERROR",
                    message = "UNKNOWN_ERROR",
                ),
            ),
            result,
        )
    }

    @Test
    fun `given a success response with an empty body when mapped then an empty body invalid response is returned`() = runTest {
        // Given
        val response = responseFor(HttpStatusCode.OK, "   ")

        // When
        val result = response.toResult(JsonObject.serializer())

        // Then
        assertEquals(
            MPResult.Failure(MPResultError.InvalidResponse(httpStatus = 200, reason = "EMPTY_BODY")),
            result,
        )
    }

    @Test
    fun `given a success response with a body that does not match the type when mapped then a decoding invalid response is returned`() = runTest {
        // Given
        val response = responseFor(HttpStatusCode.OK, "[1, 2, 3]")

        // When
        val result = response.toResult(JsonObject.serializer())

        // Then
        assertEquals(
            MPResult.Failure(MPResultError.InvalidResponse(httpStatus = 200, reason = "DECODING_FAILED")),
            result,
        )
    }

    @Test
    fun `given a success response with an unknown field when mapped to a typed class then the known field is decoded`() = runTest {
        // Given
        val response = responseFor(HttpStatusCode.OK, """{"id":"1234","unknown":"value"}""")

        // When
        val result = response.toResult(TypedItem.serializer())

        // Then
        assertEquals(MPResult.Success(TypedItem(id = "1234")), result)
    }

    @Test
    fun `given an error response with a numeric code when mapped then the code is kept as text`() = runTest {
        // Given
        val response = responseFor(HttpStatusCode.BadRequest, """{"message":"invalid","code":400}""")

        // When
        val result = response.toResult(JsonObject.serializer())

        // Then
        assertEquals(
            MPResult.Failure(MPResultError.Api(httpStatus = 400, code = "400", message = "invalid")),
            result,
        )
    }
}

@Serializable
private data class TypedItem(val id: String)
