package com.nmd.eventCalendar.compose.util

import com.nmd.eventCalendar.compose.model.CalendarDay

/**
 * Formats [value] with at least two digits, padding with a leading zero (e.g. `7` -> `"07"`).
 *
 * Platform-neutral replacement for `"%02d".format(value)`, which is JVM-only and, depending on the
 * device locale, may even render non-ASCII digits (e.g. Arabic-Indic numerals).
 */
internal fun twoDigits(value: Int): String = value.toString().padStart(2, '0')

/**
 * Screen-reader label of a day cell: the date followed by the names of the events on that day,
 * e.g. `"14 October 2026: Meeting, Gym"`.
 *
 * @param day The day the cell represents.
 * @param monthName The localized name of the day's month (see [monthDisplayName]).
 */
internal fun dayCellLabel(day: CalendarDay, monthName: String): String = buildString {
    append(day.date.day).append(' ').append(monthName).append(' ').append(day.date.year)
    if (day.events.isNotEmpty()) {
        append(": ")
        day.events.joinTo(this, separator = ", ") { it.name }
    }
}
