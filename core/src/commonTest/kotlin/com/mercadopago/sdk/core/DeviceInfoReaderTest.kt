package com.mercadopago.sdk.core

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DeviceInfoReaderTest {

    @Test
    fun `given a provider with a valid JSON object when readDeviceInfo is called then it returns the object`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { """{"os_version":"14","screen":{"width":1080}}""" }

        // When
        val deviceInfo = readDeviceInfo(deviceInfoProvider)

        // Then
        val expected = buildJsonObject {
            put("os_version", "14")
            put("screen", buildJsonObject { put("width", 1080) })
        }
        assertEquals(expected, deviceInfo)
        assertEquals(JsonPrimitive("14"), deviceInfo?.get("os_version"))
    }

    @Test
    fun `given no provider when readDeviceInfo is called then it returns null`() {
        // Given
        val deviceInfoProvider: DeviceInfoProvider? = null

        // When
        val deviceInfo = readDeviceInfo(deviceInfoProvider)

        // Then
        assertNull(deviceInfo)
    }

    @Test
    fun `given a provider that returns null when readDeviceInfo is called then it returns null`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { null }

        // When
        val deviceInfo = readDeviceInfo(deviceInfoProvider)

        // Then
        assertNull(deviceInfo)
    }

    @Test
    fun `given a provider with invalid JSON when readDeviceInfo is called then it returns null`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { """{"os_version":""" }

        // When
        val deviceInfo = readDeviceInfo(deviceInfoProvider)

        // Then
        assertNull(deviceInfo)
    }

    @Test
    fun `given a provider with a blank text when readDeviceInfo is called then it returns null`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { " " }

        // When
        val deviceInfo = readDeviceInfo(deviceInfoProvider)

        // Then
        assertNull(deviceInfo)
    }

    @Test
    fun `given a provider with a JSON array when readDeviceInfo is called then it returns null`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { """[{"os_version":"14"}]""" }

        // When
        val deviceInfo = readDeviceInfo(deviceInfoProvider)

        // Then
        assertNull(deviceInfo)
    }

    @Test
    fun `given a provider with a JSON primitive when readDeviceInfo is called then it returns null`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { "\"device\"" }

        // When
        val deviceInfo = readDeviceInfo(deviceInfoProvider)

        // Then
        assertNull(deviceInfo)
    }

    @Test
    fun `given a provider that throws when readDeviceInfo is called then it returns null`() {
        // Given
        val deviceInfoProvider = DeviceInfoProvider { throw IllegalStateException("device library failure") }

        // When
        val deviceInfo = readDeviceInfo(deviceInfoProvider)

        // Then
        assertNull(deviceInfo)
    }
}
