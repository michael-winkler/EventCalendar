package com.nmd.eventCalendar.compose.util

import com.nmd.eventCalendar.compose.model.CalendarDay
import java.time.DayOfWeek
import java.time.YearMonth

fun generateMonthDays(
    yearMonth: YearMonth,
    weekStart: DayOfWeek = DayOfWeek.MONDAY
): List<CalendarDay> {

    val firstDayOfMonth = yearMonth.atDay(1)

    val startOffset = (7 + (firstDayOfMonth.dayOfWeek.value - weekStart.value)) % 7

    val startDate = firstDayOfMonth.minusDays(startOffset.toLong())

    return (0 until 42).map { index ->
        val date = startDate.plusDays(index.toLong())
        CalendarDay(
            date = date,
            isCurrentMonth = date.month == yearMonth.month
        )
    }
}