package com.nmd.eventCalendar.compose.model

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean
)