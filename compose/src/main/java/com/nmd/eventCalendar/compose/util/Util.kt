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
import java.time.temporal.TemporalAdjusters

/**
 * Generates a list of [CalendarDay] objects for a given month or the current week.
 *
 * If [isCurrentWeekOnly] is true, it returns exactly 7 days starting from the beginning
 * of the current week (based on [weekStart]).
 * Otherwise, it returns 42 days (6 weeks) to fill a standard month grid, starting from
 * the appropriate offset to align with [weekStart].
 *
 * @param yearMonth The month to generate days for.
 * @param weekStart The first day of the week (e.g., Monday).
 * @param eventsByDate A map of events keyed by their date.
 * @param isCurrentWeekOnly If true, only the 7 days of the current week are returned.
 * @return A list of [CalendarDay] objects.
 */
internal fun generateMonthDays(
    yearMonth: YearMonth,
    weekStart: DayOfWeek = DayOfWeek.MONDAY,
    eventsByDate: Map<LocalDate, List<Event>> = emptyMap(),
    isCurrentWeekOnly: Boolean = false
): List<CalendarDay> {
    if (isCurrentWeekOnly) {
        val today = LocalDate.now()
        // Find the start of the current week
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(weekStart))

        return (0 until 7).map { index ->
            val date = startOfWeek.plusDays(index.toLong())
            CalendarDay(
                date = date,
                isCurrentMonth = (date.year == yearMonth.year && date.month == yearMonth.month),
                events = eventsByDate[date].orEmpty()
            )
        }
    }

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

/**
 * Returns true if the current window is considered a tablet based on its smallest dimension.
 * A window is a tablet if its minimum dimension is at least 600dp.
 */
@Composable
internal fun isTabletWindow(): Boolean {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthDp = with(density) { windowInfo.containerSize.width.toDp() }
    val heightDp = with(density) { windowInfo.containerSize.height.toDp() }
    return minOf(widthDp, heightDp) >= 600.dp
}

/**
 * Returns true if the current window is considered a phone (not a tablet).
 */
@Composable
internal fun isPhoneWindow(): Boolean = !isTabletWindow()

/**
 * Returns true if the current window is in landscape orientation (width >= height).
 */
@Composable
internal fun isLandscapeWindow(): Boolean {
    val windowInfo = LocalWindowInfo.current
    return windowInfo.containerSize.width >= windowInfo.containerSize.height
}

/**
 * Returns true if the current window is a phone in landscape orientation.
 */
@Composable
internal fun isPhoneLandscapeWindow(): Boolean = isPhoneWindow() && isLandscapeWindow()