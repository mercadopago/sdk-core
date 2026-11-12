package com.mercadopago.sdk.common

/**
 * Marks declarations shared between the Mercado Pago SDK modules that are not part of the
 * integration API.
 *
 * They can change or be removed in any version, without notice. Integrators must not use them.
 */
@RequiresOptIn(
    message = "Internal API of the Mercado Pago SDK modules. Do not use it in integrations.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS,
)
public annotation class InternalMercadoPagoApi
