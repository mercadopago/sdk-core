package com.mercadopago.sdk.core

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

internal actual fun systemCalendarMonth(): CalendarMonth {
    val calendar = NSCalendar.calendarWithIdentifier(NSCalendarIdentifierGregorian) ?: NSCalendar.currentCalendar
    val components = calendar.components(NSCalendarUnitYear or NSCalendarUnitMonth, fromDate = NSDate())
    return CalendarMonth(year = components.year.toInt(), month = components.month.toInt())
}
