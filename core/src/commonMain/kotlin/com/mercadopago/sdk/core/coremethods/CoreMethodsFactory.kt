package com.mercadopago.sdk.core.coremethods

import com.mercadopago.sdk.network.api.ApiClient

internal fun createCoreMethods(apiClient: ApiClient): CoreMethods =
    CoreMethods(identificationTypesRepository = createIdentificationTypesRepository(apiClient))
