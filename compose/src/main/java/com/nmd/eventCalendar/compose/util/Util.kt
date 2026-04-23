package com.nmd.eventCalendar.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventcalendar.compose.R
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

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
    val today =
        kotlin.time.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
    if (isCurrentWeekOnly) {
        // Find the start of the current week
        val daysUntil = (today.dayOfWeek.ordinal - weekStart.ordinal + 7) % 7
        val startOfWeek = today.minus(daysUntil, kotlinx.datetime.DateTimeUnit.DAY)

        return (0 until 7).map { index ->
            val date = startOfWeek.plus(index, kotlinx.datetime.DateTimeUnit.DAY)
            CalendarDay(
                date = date,
                isCurrentMonth = (date.year == yearMonth.year && date.month == yearMonth.month),
                events = eventsByDate[date].orEmpty()
            )
        }
    }

    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = (7 + (firstDayOfMonth.dayOfWeek.ordinal - weekStart.ordinal)) % 7
    val startDate = firstDayOfMonth.minus(startOffset, kotlinx.datetime.DateTimeUnit.DAY)

    return (0 until 42).map { index ->
        val date = startDate.plus(index, kotlinx.datetime.DateTimeUnit.DAY)
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
 * Returns the string resource ID for the short name of this day of the week.
 */
fun DayOfWeek.toStringRes(): Int = when (this) {
    DayOfWeek.MONDAY -> R.string.day_name_monday
    DayOfWeek.TUESDAY -> R.string.day_name_tuesday
    DayOfWeek.WEDNESDAY -> R.string.day_name_wednesday
    DayOfWeek.THURSDAY -> R.string.day_name_thursday
    DayOfWeek.FRIDAY -> R.string.day_name_friday
    DayOfWeek.SATURDAY -> R.string.day_name_saturday
    DayOfWeek.SUNDAY -> R.string.day_name_sunday
}

/**
 * Returns the string resource ID for the full name of this month.
 */
fun Month.toStringRes(): Int = when (this) {
    Month.JANUARY -> R.string.month_january
    Month.FEBRUARY -> R.string.month_february
    Month.MARCH -> R.string.month_march
    Month.APRIL -> R.string.month_april
    Month.MAY -> R.string.month_may
    Month.JUNE -> R.string.month_june
    Month.JULY -> R.string.month_july
    Month.AUGUST -> R.string.month_august
    Month.SEPTEMBER -> R.string.month_september
    Month.OCTOBER -> R.string.month_october
    Month.NOVEMBER -> R.string.month_november
    Month.DECEMBER -> R.string.month_december
}

/**
 * Returns the ISO 8601 week number for this date.
 */
internal fun LocalDate.isoWeekNumber(): Int {
    // A simplified ISO week calculation
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