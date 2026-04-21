package com.nmd.eventCalendar.compose.model

import kotlinx.datetime.LocalDate

/**
 * Represents a single day cell in the calendar UI.
 *
 * A [CalendarDay] contains the actual [date], information whether that date belongs to the
 * currently displayed month ([isCurrentMonth]), and the list of [events] associated with that day.
 *
 * Typically, days that are not part of the current month are shown as leading/trailing padding
 * days in a month grid and can be styled differently.
 *
 * @property date The represented calendar date.
 * @property isCurrentMonth Whether [date] belongs to the currently displayed month.
 * @property events Events occurring on this [date]. Defaults to an empty list.
 */
data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val events: List<Event> = emptyList()
)