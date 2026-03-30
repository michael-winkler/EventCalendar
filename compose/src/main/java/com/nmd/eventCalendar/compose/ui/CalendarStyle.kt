package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class CalendarStyle(
    val monthNavigationIconColor: Color,
    val monthNameTextColor: Color,

    val currentWeekDayTextColor: Color,
    val defaultWeekDayTextColor: Color,

    /**
     * Days in current month
     */
    val weekDayTextColor: Color,
    /**
     * Past or next month
     */
    val weekDayInactiveTextColor: Color,

    val weekItemTextColor: Color,
    val weekItemBackgroundColor: Color
)

@Composable
fun defaultCalendarStyle(): CalendarStyle {
    return CalendarStyle(
        monthNavigationIconColor = monthNavigationIconColor,
        monthNameTextColor = monthNameTextColor,
        currentWeekDayTextColor = currentWeekDayTextColor,
        defaultWeekDayTextColor = defaultWeekDayTextColor,
        weekDayTextColor = weekDayTextColor,
        weekDayInactiveTextColor = weekDayInactiveTextColor,
        weekItemTextColor = weekItemTextColor,
        weekItemBackgroundColor = weekItemBackgroundColor
    )
}

val currentWeekDayTextColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.Green else Color.Blue

val defaultWeekDayTextColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val weekDayTextColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val weekDayInactiveTextColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.LightGray else Color.Gray

val monthNavigationIconColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val monthNameTextColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val weekItemTextColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val weekItemBackgroundColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF3D4043) else Color(0xFFDBDCE0)