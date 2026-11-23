package com.mercadopago.sdk.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal fun readDeviceInfo(deviceInfoProvider: DeviceInfoProvider?): JsonObject? {
    if (deviceInfoProvider == null) {
        return null
    }
    return try {
        val deviceInfo = deviceInfoProvider.deviceInfo() ?: return null
        Json.parseToJsonElement(deviceInfo) as? JsonObject
    } catch (exception: Exception) {
        null
    }
}
