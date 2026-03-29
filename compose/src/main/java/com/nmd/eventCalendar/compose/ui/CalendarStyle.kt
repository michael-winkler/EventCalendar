package com.nmd.eventCalendar.compose.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class CalendarStyle(
    val currentWeekDayTextColor: Color,
    val defaultWeekDayTextColor: Color,
    val weekLabelColor: Color
)

@Composable
fun defaultCalendarStyle(): CalendarStyle {
    return CalendarStyle(
        currentWeekDayTextColor = MaterialTheme.colorScheme.primary,
        defaultWeekDayTextColor = MaterialTheme.colorScheme.onBackground,
        weekLabelColor = MaterialTheme.colorScheme.onBackground
    )
}