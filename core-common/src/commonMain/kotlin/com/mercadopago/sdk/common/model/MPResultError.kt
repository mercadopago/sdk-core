package com.mercadopago.sdk.common.model

/**
 * Errors returned by the core operations.
 */
public sealed class MPResultError {

    /**
     * The API answered with a non-success HTTP status and an error body.
     * @property httpStatus HTTP status code of the response.
     * @property code Identifier of the error returned by the API, or `UNKNOWN_ERROR` when the body has none.
     * @property message Description of the error returned by the API.
     * @property errorCode Specific error identifier returned by the API (`error_code`), when present.
     * @property userErrorMessage Message that can be shown to the user (`user_error_message`), when present.
     */
    public data class Api(
        val httpStatus: Int,
        val code: String,
        val message: String,
        val errorCode: String? = null,
        val userErrorMessage: String? = null,
    ) : MPResultError()

    /**
     * The request failed before a valid HTTP response was received (timeout, no connection, etc.).
     * @property networkErrorType Category of the transport failure.
     */
    public data class Network(
        val networkErrorType: NetworkErrorType,
    ) : MPResultError()

    /**
     * The API answered with a success status, but the body is empty or does not match the expected format.
     * @property httpStatus HTTP status code of the response.
     * @property reason Category of the rejection, for example `EMPTY_BODY` or `DECODING_FAILED`.
     * @property fieldPath Path of the field that failed to decode, with names only and no values, when known.
     */
    public data class InvalidResponse(
        val httpStatus: Int,
        val reason: String,
        val fieldPath: String? = null,
    ) : MPResultError()

    /**
     * Failure of an input validation, detected before calling the API.
     * @property message Description of the validation error.
     */
    public data class Validation(
        val message: String,
    ) : MPResultError()
}
