package com.nmd.eventCalendar.compose.ui

import androidx.compose.runtime.Composable
import java.time.DayOfWeek

data class CalendarOptions(
    val weekStart: DayOfWeek,
    val headerVisible: Boolean,
    val calendarWeekVisible: Boolean
)

@Composable
fun defaultCalendarOptions(): CalendarOptions {
    return CalendarOptions(
        weekStart = DayOfWeek.MONDAY,
        headerVisible = true,
        calendarWeekVisible = false
    )
}