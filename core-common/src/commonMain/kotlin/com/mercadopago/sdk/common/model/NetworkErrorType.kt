package com.mercadopago.sdk.common.model

/**
 * Category of a transport failure, reported by [MPResultError.Network].
 */
public enum class NetworkErrorType {
    /** The request or the connection timed out. */
    TIMEOUT,

    /** There was no connection to the server. */
    NO_CONNECTION,

    /** Any other transport failure. */
    UNKNOWN,
}
