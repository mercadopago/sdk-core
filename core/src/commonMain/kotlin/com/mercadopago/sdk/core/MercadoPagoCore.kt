package com.mercadopago.sdk.core

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.createCoreMethods
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.createApiClient

private const val PUBLIC_KEY_PARAMETER = "public_key"
private const val BLANK_PUBLIC_KEY_MESSAGE = "The public key must not be blank"

/**
 * Entry point of the services and business rules shared by every Mercado Pago SDK.
 * The SDKs only provide the frontend and create this instance with [Builder].
 */
public class MercadoPagoCore internal constructor(
    private val apiClient: ApiClient,
) {

    private val coreMethods = createCoreMethods(apiClient)

    /**
     * Returns the identification document types of the payment form, such as CPF or DNI.
     */
    public suspend fun getIdentificationTypes(): MPResult<List<IdentificationType>, MPResultError> =
        coreMethods.getIdentificationTypes()

    /**
     * Releases the network resources. The instance must not be used after this call.
     */
    public fun close() {
        apiClient.close()
    }

    /**
     * Builds a [MercadoPagoCore] instance for an SDK integration.
     *
     * Example:
     * ```
     * val core = MercadoPagoCore.Builder(publicKey = "...").build()
     * ```
     */
    public class Builder(
        private val publicKey: String,
    ) {

        /**
         * Creates the [MercadoPagoCore] instance.
         * @throws IllegalArgumentException if the public key is blank.
         */
        public fun build(): MercadoPagoCore {
            require(publicKey.isNotBlank()) { BLANK_PUBLIC_KEY_MESSAGE }
            val apiClient = createApiClient(
                defaultQueryParameters = { mapOf(PUBLIC_KEY_PARAMETER to publicKey) },
            )
            return MercadoPagoCore(apiClient)
        }
    }
}
