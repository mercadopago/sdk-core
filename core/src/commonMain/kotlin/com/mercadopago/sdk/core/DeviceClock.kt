package com.mercadopago.sdk.core

internal fun interface DeviceClock {
    fun currentCalendarMonth(): CalendarMonth
}
