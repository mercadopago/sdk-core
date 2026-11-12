package com.mercadopago.sdk.common.model

/**
 * Result of a core operation: either [Success] with the data or [Failure] with the error.
 *
 * Expected API and network failures are returned as [Failure], never thrown.
 */
public sealed class MPResult<out S, out E> {

    /**
     * Successful result.
     * @property data The value returned by the operation.
     */
    public data class Success<S>(val data: S) : MPResult<S, Nothing>()

    /**
     * Failed result.
     * @property error The error that made the operation fail.
     */
    public data class Failure<E>(val error: E) : MPResult<Nothing, E>()
}
