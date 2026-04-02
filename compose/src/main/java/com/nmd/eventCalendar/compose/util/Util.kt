package com.nmd.eventCalendar.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

internal fun generateMonthDays(
    yearMonth: YearMonth,
    weekStart: DayOfWeek = DayOfWeek.MONDAY,
    eventsByDate: Map<LocalDate, List<Event>> = emptyMap()
): List<CalendarDay> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = (7 + (firstDayOfMonth.dayOfWeek.value - weekStart.value)) % 7
    val startDate = firstDayOfMonth.minusDays(startOffset.toLong())

    return (0 until 42).map { index ->
        val date = startDate.plusDays(index.toLong())
        CalendarDay(
            date = date,
            isCurrentMonth = (date.year == yearMonth.year && date.month == yearMonth.month),
            events = eventsByDate[date].orEmpty()
        )
    }
}

@Composable
internal fun isPortrait(): Boolean {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthDp = with(density) { windowInfo.containerSize.width.toDp() }
    return widthDp < 600.dp
}

@Composable
internal fun isLandscape(): Boolean {
    return !isPortrait()
}