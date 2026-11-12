package com.mercadopago.sdk.common.model

internal inline fun <T, R, E> MPResult<T, E>.mapSuccess(transform: (T) -> R): MPResult<R, E> =
    when (this) {
        is MPResult.Success -> MPResult.Success(transform(data))
        is MPResult.Failure -> this
    }
