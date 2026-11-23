package com.mercadopago.sdk.core

/**
 * Provides the information of the device, such as the model and the operating system, sent when a card token is created.
 *
 * Each platform SDK implements it with its device library and passes it to [MercadoPagoCore.Builder.deviceInfoProvider].
 * The provider is optional: without it, the card token is created without the device information.
 */
public fun interface DeviceInfoProvider {

    /**
     * Returns the device information as a JSON object with snake_case keys, or `null` when it is not available.
     *
     * When the value is `null`, is not valid JSON or is not a JSON object, the card token is created without the
     * device information and no error is returned.
     */
    public fun deviceInfo(): String?
}
