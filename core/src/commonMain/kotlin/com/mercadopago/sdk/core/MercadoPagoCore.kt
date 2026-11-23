package com.mercadopago.sdk.core

import com.mercadopago.sdk.common.model.MPResult
import com.mercadopago.sdk.common.model.MPResultError
import com.mercadopago.sdk.core.coremethods.createCoreMethods
import com.mercadopago.sdk.core.coremethods.domain.model.CardData
import com.mercadopago.sdk.core.coremethods.domain.model.CardIssuer
import com.mercadopago.sdk.core.coremethods.domain.model.CardToken
import com.mercadopago.sdk.core.coremethods.domain.model.IdentificationType
import com.mercadopago.sdk.core.coremethods.domain.model.Installment
import com.mercadopago.sdk.core.coremethods.domain.model.PaymentMethod
import com.mercadopago.sdk.core.coremethods.domain.model.SavedCardData
import com.mercadopago.sdk.network.api.ApiClient
import com.mercadopago.sdk.network.createApiClient

private const val PUBLIC_KEY_PARAMETER = "public_key"
private const val BLANK_PUBLIC_KEY_MESSAGE = "The public key must not be blank"
private const val BLANK_PRODUCT_ID_MESSAGE = "The product id must not be blank"
private const val INVALID_BIN_MESSAGE = "The BIN must have 6 to 8 digits"
private const val BLANK_PAYMENT_METHOD_ID_MESSAGE = "The payment method id must not be blank"
private const val INVALID_AMOUNT_MESSAGE = "The amount must be a number from 0.01 to 10000000000000"
private const val MIN_AMOUNT = 0.01
private const val MAX_AMOUNT = 10_000_000_000_000.0
private val BIN_PATTERN = Regex("[0-9]{6,8}")

/**
 * Entry point of the services and business rules shared by every Mercado Pago SDK.
 * The SDKs only provide the frontend and create this instance with [Builder].
 */
public class MercadoPagoCore internal constructor(
    private val apiClient: ApiClient,
    internal val config: CoreConfig,
    private val deviceClock: DeviceClock = DeviceClock { systemCalendarMonth() },
) {

    internal val sessionId: String = generateSessionId()

    private val coreMethods = createCoreMethods(apiClient, config.deviceInfoProvider, deviceClock)

    /**
     * Returns the identification document types of the payment form, such as CPF or DNI.
     */
    public suspend fun getIdentificationTypes(): MPResult<List<IdentificationType>, MPResultError> =
        coreMethods.getIdentificationTypes()

    /**
     * Returns the payment methods that match the BIN of the card, such as Visa or Mastercard.
     *
     * The first method of the list always has card rules. If the API does not send them, this call returns [MPResultError.InvalidResponse].
     *
     * @param bin First 6 to 8 digits of the card number.
     * @return [MPResultError.Validation] when the BIN is not 6 to 8 digits; the API is not called in this case.
     */
    public suspend fun getPaymentMethods(bin: String): MPResult<List<PaymentMethod>, MPResultError> {
        if (!BIN_PATTERN.matches(bin)) {
            return MPResult.Failure(MPResultError.Validation(message = INVALID_BIN_MESSAGE))
        }
        return coreMethods.getPaymentMethods(productId = config.productId, bin = bin)
    }

    /**
     * Returns the card issuers, such as banks, that accept the payment method for the BIN.
     *
     * @param bin First 6 to 8 digits of the card number.
     * @param paymentMethodId Identifier of the payment method, such as "visa".
     * @return [MPResultError.Validation] when the BIN is not 6 to 8 digits or the payment method id is blank; the API is not called in this case.
     */
    public suspend fun getCardIssuers(bin: String, paymentMethodId: String): MPResult<List<CardIssuer>, MPResultError> {
        if (!BIN_PATTERN.matches(bin)) {
            return MPResult.Failure(MPResultError.Validation(message = INVALID_BIN_MESSAGE))
        }
        if (paymentMethodId.isBlank()) {
            return MPResult.Failure(MPResultError.Validation(message = BLANK_PAYMENT_METHOD_ID_MESSAGE))
        }
        return coreMethods.getCardIssuers(productId = config.productId, bin = bin, paymentMethodId = paymentMethodId)
    }

    /**
     * Returns the installment options, such as 1, 3 or 6 installments, of the payment methods that match the BIN for the amount.
     *
     * The amount is sent with two decimal places. The processing mode is not sent, so the API uses its default, aggregator.
     * When no installments are found, the API answers HTTP 404, returned as [MPResultError.Api]; an empty successful
     * response is returned as an empty list.
     *
     * @param bin First 6 to 8 digits of the card number.
     * @param amount Amount to pay, in the currency of the site, from 0.01 to 10000000000000. The limit keeps the conversion to cents exact.
     * @return [MPResultError.Validation] when the BIN is not 6 to 8 digits or the amount is out of range; the API is not called in this case.
     */
    public suspend fun getInstallments(bin: String, amount: Double): MPResult<List<Installment>, MPResultError> {
        if (!BIN_PATTERN.matches(bin)) {
            return MPResult.Failure(MPResultError.Validation(message = INVALID_BIN_MESSAGE))
        }
        if (!amount.isFinite() || amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
            return MPResult.Failure(MPResultError.Validation(message = INVALID_AMOUNT_MESSAGE))
        }
        return coreMethods.getInstallments(productId = config.productId, bin = bin, amount = amount)
    }

    /**
     * Creates a card token for a new card, to send in the payment instead of the card data.
     *
     * The card data is validated before the API is called, in this order: card number, expiration date and
     * security code. Only the first error is returned, and its message never contains the typed values.
     * The request is not retried, so a failure never creates a second token.
     *
     * @param cardData Data of the card, as typed by the buyer.
     * @param securityCodeLength Expected length of the security code for the card brand, or `null` when it is not
     * known. With a length, the security code must have exactly that many digits; without it, 3 or 4 digits.
     * @return [MPResult.Success] with the [CardToken], or [MPResult.Failure] with:
     * - [MPResultError.Validation] when the card number is not digits only or fails the Luhn check; when the
     * expiration date is not in the MM/YY or MM/YYYY format; when the expiration month is not from 01 to 12;
     * when the card expired before the current month, in the time zone of the device; or when the security code
     * is not digits only or does not have the expected length. The API is not called in these cases.
     * - [MPResultError.Api] when the API answers with an HTTP status outside 2xx. The message is always
     * `Card token request failed` and `userErrorMessage` is always `null`. `code` and `errorCode` come from the API
     * only when they are identifiers (letters, digits, `_`, `.` and `-`, up to 64 characters) that contain none of
     * the sent card data; otherwise they are `UNKNOWN_ERROR` and `null`.
     * - [MPResultError.InvalidResponse] when the API answers 2xx with an empty body (`EMPTY_BODY`) or without the
     * token (`DECODING_FAILED`).
     * - [MPResultError.Network] when the request fails before a response is received, such as a timeout or no
     * connection.
     */
    public suspend fun createCardToken(
        cardData: CardData,
        securityCodeLength: Int?,
    ): MPResult<CardToken, MPResultError> {
        return coreMethods.createCardToken(
            productId = config.productId,
            sessionId = sessionId,
            cardData = cardData,
            securityCodeLength = securityCodeLength,
        )
    }

    /**
     * Creates a card token for a saved card, with the security code typed by the buyer, to send in the payment
     * instead of the card data.
     *
     * Only the security code is validated before the API is called, and its message never contains the typed
     * value. The request is not retried, so a failure never creates a second token.
     *
     * @param savedCardData Identifier of the saved card and the security code typed by the buyer.
     * @param securityCodeLength Expected length of the security code for the card brand, or `null` when it is not
     * known. With a length, the security code must have exactly that many digits; without it, 3 or 4 digits.
     * @return [MPResult.Success] with the [CardToken], or [MPResult.Failure] with:
     * - [MPResultError.Validation] when the security code is not digits only or does not have the expected length.
     * The API is not called in this case.
     * - [MPResultError.Api] when the API answers with an HTTP status outside 2xx. The message is always
     * `Card token request failed` and `userErrorMessage` is always `null`. `code` and `errorCode` come from the API
     * only when they are identifiers (letters, digits, `_`, `.` and `-`, up to 64 characters) that contain none of
     * the sent card data; otherwise they are `UNKNOWN_ERROR` and `null`.
     * - [MPResultError.InvalidResponse] when the API answers 2xx with an empty body (`EMPTY_BODY`) or without the
     * token (`DECODING_FAILED`).
     * - [MPResultError.Network] when the request fails before a response is received, such as a timeout or no
     * connection.
     */
    public suspend fun createCardToken(
        savedCardData: SavedCardData,
        securityCodeLength: Int?,
    ): MPResult<CardToken, MPResultError> {
        return coreMethods.createCardToken(
            productId = config.productId,
            sessionId = sessionId,
            savedCardData = savedCardData,
            securityCodeLength = securityCodeLength,
        )
    }

    /**
     * Releases the network resources. The instance must not be used after this call.
     */
    public fun close() {
        apiClient.close()
    }

    /**
     * Builds a [MercadoPagoCore] instance for an SDK integration.
     *
     * Create one instance per checkout session and reuse it for every call. The `Meli-Session-id` header is generated
     * per instance, so a new instance sends a different session id in the middle of the same session.
     *
     * Example:
     * ```
     * val core = MercadoPagoCore.Builder(publicKey = "...", productId = "...")
     *     .deviceInfoProvider(deviceInfoProvider) // optional
     *     .build()
     * ```
     */
    public class Builder(
        private val publicKey: String,
        private val productId: String,
    ) {

        private var deviceInfoProvider: DeviceInfoProvider? = null

        /**
         * Sets the provider of the device information sent when a card token is created.
         *
         * Optional: without a provider, the card token is created without the device information.
         * @param deviceInfoProvider Provider of the device information, implemented by the platform SDK.
         * @return This builder, to chain the next calls.
         */
        public fun deviceInfoProvider(deviceInfoProvider: DeviceInfoProvider): Builder {
            this.deviceInfoProvider = deviceInfoProvider
            return this
        }

        /**
         * Creates the [MercadoPagoCore] instance.
         * @throws IllegalArgumentException if the public key or the product id is blank.
         */
        public fun build(): MercadoPagoCore {
            require(publicKey.isNotBlank()) { BLANK_PUBLIC_KEY_MESSAGE }
            require(productId.isNotBlank()) { BLANK_PRODUCT_ID_MESSAGE }
            val apiClient = createApiClient(
                defaultQueryParameters = { mapOf(PUBLIC_KEY_PARAMETER to publicKey) },
            )
            val config = CoreConfig(productId = productId, deviceInfoProvider = deviceInfoProvider)
            return MercadoPagoCore(apiClient = apiClient, config = config)
        }
    }
}
