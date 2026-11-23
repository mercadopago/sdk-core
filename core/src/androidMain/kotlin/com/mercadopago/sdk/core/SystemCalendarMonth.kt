package com.mercadopago.sdk.core

import java.util.Calendar
import java.util.GregorianCalendar

internal actual fun systemCalendarMonth(): CalendarMonth {
    val calendar = GregorianCalendar()
    return CalendarMonth(year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH) + 1)
}
