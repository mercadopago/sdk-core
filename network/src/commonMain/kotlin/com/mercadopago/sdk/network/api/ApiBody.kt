package com.mercadopago.sdk.network.api

import com.mercadopago.sdk.common.InternalMercadoPagoApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

@InternalMercadoPagoApi
public class ApiBody<B>(
    internal val value: B,
    internal val serializer: SerializationStrategy<B>,
) {
    internal fun encode(json: Json): String = json.encodeToString(serializer, value)
}
