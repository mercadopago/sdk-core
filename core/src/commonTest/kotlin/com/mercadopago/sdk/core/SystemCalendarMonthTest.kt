package com.mercadopago.sdk.core

import kotlin.test.Test
import kotlin.test.assertTrue

internal class SystemCalendarMonthTest {

    @Test
    fun `given the device clock when systemCalendarMonth is called then it returns a Gregorian year and a month from 1 to 12`() {
        // Given
        val firstMonth = 1
        val lastMonth = 12

        // When
        val calendarMonth = systemCalendarMonth()

        // Then
        assertTrue(calendarMonth.month in firstMonth..lastMonth)
        assertTrue(calendarMonth.year in 2026..2999)
    }
}
