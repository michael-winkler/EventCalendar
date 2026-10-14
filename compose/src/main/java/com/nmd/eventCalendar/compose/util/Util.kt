package com.nmd.eventCalendar.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.YearMonth
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
 * @param today The date treated as "today" (anchors the week in [isCurrentWeekOnly] mode). Pass a
 * value from [rememberToday] in composables so the grid follows the date across midnight.
 * @return A list of [CalendarDay] objects.
 */
internal fun generateMonthDays(
    yearMonth: YearMonth,
    weekStart: DayOfWeek = DayOfWeek.MONDAY,
    eventsByDate: Map<LocalDate, List<Event>> = emptyMap(),
    isCurrentWeekOnly: Boolean = false,
    today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
): List<CalendarDay> {
    if (isCurrentWeekOnly) {
        // Find the start of the current week
        val daysUntil = (today.dayOfWeek.ordinal - weekStart.ordinal + 7) % 7
        val startOfWeek = today.minus(daysUntil, DateTimeUnit.DAY)

        return (0 until 7).map { index ->
            val date = startOfWeek.plus(index, DateTimeUnit.DAY)
            CalendarDay(
                date = date,
                isCurrentMonth = (date.year == yearMonth.year && date.month == yearMonth.month),
                events = eventsByDate[date].orEmpty()
            )
        }
    }

    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = (7 + (firstDayOfMonth.dayOfWeek.ordinal - weekStart.ordinal)) % 7
    val startDate = firstDayOfMonth.minus(startOffset, DateTimeUnit.DAY)

    return (0 until 42).map { index ->
        val date = startDate.plus(index, DateTimeUnit.DAY)
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

/**
 * Returns the day of the week that is [days] after this one.
 */
internal fun DayOfWeek.plus(days: Long): DayOfWeek {
    val values = DayOfWeek.entries
    val newOrdinal = (ordinal + (days % 7).toInt() + 7) % 7
    return values[newOrdinal]
}

/**
 * Returns the ISO 8601 week number for this date.
 */
internal fun LocalDate.isoWeekNumber(): Int {
    // ISO weeks are identified by their Thursday: the week belongs to the year its Thursday falls in,
    // and week 1 is the week containing the year's first Thursday. Both Thursdays below therefore lie
    // in the same year, so the day-of-year difference is exact (also across year boundaries).
    // See: https://en.wikipedia.org/wiki/ISO_week_date#Calculating_the_week_number_of_a_given_date
    val thursdayThisWeek = this.plus((4 - this.dayOfWeek.isoDayNumber), DateTimeUnit.DAY)
    val firstDayOfYear = LocalDate(thursdayThisWeek.year, 1, 1)
    val thursdayFirstWeek = firstDayOfYear.plus(
        (4 - firstDayOfYear.dayOfWeek.isoDayNumber).let { if (it < 0) it + 7 else it },
        DateTimeUnit.DAY
    )

    val diffDays = (thursdayThisWeek.dayOfYear - thursdayFirstWeek.dayOfYear)
    return (diffDays / 7) + 1
}

/**
 * Returns the ISO 8601 week number to display for a calendar row that starts on [rowStart].
 *
 * A row always shows 7 consecutive days, but it only coincides with an ISO week (Monday–Sunday) when
 * the configured week start is Monday. For any other week start the row straddles two ISO weeks, so
 * the number is taken from the row's Thursday: it is the day that determines the ISO week, and with
 * at least four of the row's days sharing that ISO week it is the number users expect to see.
 *
 * @param rowStart The first date shown in the row.
 * @return The ISO week number that represents the row.
 */
internal fun isoWeekNumberOfRow(rowStart: LocalDate): Int {
    val offsetToThursday =
        (DayOfWeek.THURSDAY.isoDayNumber - rowStart.dayOfWeek.isoDayNumber + 7) % 7
    return rowStart.plus(offsetToThursday, DateTimeUnit.DAY).isoWeekNumber()
}

/**
 * Returns the current date and keeps it up to date while the composable is active.
 *
 * A plain `remember { todayIn(...) }` (or a top-level value) freezes "today" at the moment it was
 * first computed, so a screen left open past midnight keeps highlighting yesterday. This re-checks
 * the clock at least once a minute and right after midnight, so the value changes on its own.
 *
 * The system time zone is re-read on every check (not captured once), so a time-zone change while
 * the screen is open (e.g. when travelling) is picked up as well.
 *
 * @return The current date in the system time zone.
 */
@Composable
internal fun rememberToday(): LocalDate {
    val today by produceState(
        initialValue = Clock.System.todayIn(TimeZone.currentSystemDefault())
    ) {
        while (true) {
            val timeZone = TimeZone.currentSystemDefault()
            val current = Clock.System.todayIn(timeZone)
            value = current
            val nextMidnight = current.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
            // Wake up at midnight at the latest, but also at least once a minute: delays are measured
            // in device uptime, which pauses while the device sleeps.
            delay((nextMidnight - Clock.System.now()).coerceIn(1.seconds, 1.minutes))
        }
    }
    return today
}

/**
 * Returns the days shown by the time-grid view.
 *
 * - A full week (7 or more requested days) shows the week containing [today], aligned to [weekStart].
 * - Shorter views (e.g. 1-day or 3-day) start with [today]. Aligning them to the week start would
 *   show e.g. only Monday in a 1-day view for the whole week, and hide today in a 3-day view from
 *   Thursday on.
 *
 * [noOfVisibleDays] is coerced into `1..7`.
 *
 * @param today The current date.
 * @param weekStart The first day of the week.
 * @param noOfVisibleDays The number of days to show.
 * @return The consecutive days to display, in order.
 */
internal fun visibleTimeGridDays(
    today: LocalDate,
    weekStart: DayOfWeek,
    noOfVisibleDays: Int
): List<LocalDate> {
    val count = noOfVisibleDays.coerceIn(1, 7)
    val start = if (count == 7) {
        val daysSinceWeekStart = (today.dayOfWeek.ordinal - weekStart.ordinal + 7) % 7
        today.minus(daysSinceWeekStart, DateTimeUnit.DAY)
    } else {
        today
    }
    return (0 until count).map { start.plus(it, DateTimeUnit.DAY) }
}
