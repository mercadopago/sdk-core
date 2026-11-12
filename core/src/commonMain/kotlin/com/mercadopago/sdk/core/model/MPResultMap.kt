package com.mercadopago.sdk.core.model

import com.mercadopago.sdk.common.model.MPResult

internal inline fun <T, R, E> MPResult<T, E>.map(transform: (T) -> R): MPResult<R, E> =
    when (this) {
        is MPResult.Success -> MPResult.Success(transform(data))
        is MPResult.Failure -> MPResult.Failure(error)
    }
