package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.core.DeviceClock
import com.mercadopago.sdk.core.DeviceInfoProvider
import com.mercadopago.sdk.core.coremethods.domain.usecase.CreateCardTokenUseCase
import com.mercadopago.sdk.core.systemCalendarMonth
import com.mercadopago.sdk.network.api.ApiClient

internal fun createCoreMethods(
    apiClient: ApiClient,
    deviceInfoProvider: DeviceInfoProvider?,
    deviceClock: DeviceClock = DeviceClock { systemCalendarMonth() },
): CoreMethods =
    CoreMethods(
        identificationTypesRepository = createIdentificationTypesRepository(apiClient),
        paymentMethodsRepository = createPaymentMethodsRepository(apiClient),
        cardIssuersRepository = createCardIssuersRepository(apiClient),
        installmentsRepository = createInstallmentsRepository(apiClient),
        createCardTokenUseCase = CreateCardTokenUseCase(
            cardTokensRepository = createCardTokensRepository(apiClient, deviceInfoProvider),
            deviceClock = deviceClock,
        ),
    )
