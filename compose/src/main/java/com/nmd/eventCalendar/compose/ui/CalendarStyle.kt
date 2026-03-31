package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Styling configuration for the calendar UI.
 *
 * This is a simple data holder that defines typography sizes and colors for the
 * different calendar parts (month header, weekdays, day cells, week number column).
 *
 * Tip:
 * If you need a theme-like approach, consider providing this via CompositionLocal.
 */
data class CalendarStyle(
    /** Default text size used across the calendar. */
    val textUnit: TextUnit,

    // --- Month header / navigation ---
    /** Tint color for month navigation icons (previous/next). */
    val monthNavigationIconColor: Color,
    /** Text color for the month name/title. */
    val monthNameTextColor: Color,

    // --- Weekday row (Mon..Sun) ---
    /** Text color for the weekday representing "today" (e.g. current weekday). */
    val currentWeekDayTextColor: Color,
    /** Default text color for weekday labels. */
    val defaultWeekDayTextColor: Color,

    /**
     * Text color for weekday labels when representing days of the current month.
     * (Kept separate in case you want different styling than [defaultWeekDayTextColor].)
     */
    val weekDayTextColor: Color,

    /** Text color for weekday/day labels that belong to previous/next month (inactive). */
    val weekDayInactiveTextColor: Color,

    // --- Day cells ---
    /** Default text color for day numbers. */
    val dayItemTextColor: Color,
    /** Default background color for day cells. */
    val dayItemBackgroundColor: Color,
    /** Text color for the current day highlight. */
    val currentDayTextColor: Color,
    /** Background color for the current day highlight. */
    val currentDayBackgroundColor: Color,

    // --- Calendar week (week number column) ---
    /** Text color for the week number items. */
    val weekItemTextColor: Color,
    /** Background color for the week number items. */
    val weekItemBackgroundColor: Color
)

/**
 * Creates a default [CalendarStyle] based on the current system theme (dark/light).
 */
@Composable
fun defaultCalendarStyle(): CalendarStyle {
    return CalendarStyle(
        textUnit = 14.sp,

        // Month header / navigation
        monthNavigationIconColor = monthNavigationIconColor,
        monthNameTextColor = monthNameTextColor,

        // Weekday row
        currentWeekDayTextColor = currentWeekDayTextColor,
        defaultWeekDayTextColor = defaultWeekDayTextColor,
        weekDayTextColor = weekDayTextColor,
        weekDayInactiveTextColor = weekDayInactiveTextColor,

        // Day cells
        dayItemTextColor = dayItemTextColor,
        dayItemBackgroundColor = dayItemBackgroundColor,
        currentDayTextColor = currentDayTextColor,
        currentDayBackgroundColor = currentDayBackgroundColor,

        // Calendar week column
        weekItemTextColor = weekItemTextColor,
        weekItemBackgroundColor = weekItemBackgroundColor
    )
}

/* ========================================================================
 * Color palette (grouped by category)
 * ===================================================================== */

/* --- Month header / navigation --- */

val monthNavigationIconColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val monthNameTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/* --- Weekday row (Mon..Sun) --- */

val currentWeekDayTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.Green else Color.Blue

val defaultWeekDayTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/**
 * Weekday label color for days that belong to the currently displayed month.
 */
val weekDayTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/**
 * Weekday/day label color for days that belong to the previous/next month.
 */
val weekDayInactiveTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.LightGray else Color.Gray

/* --- Day cells --- */

val dayItemTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val dayItemBackgroundColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF282A2C) else Color(0xFFF4F4F4)

val currentDayTextColor: Color
    @Composable get() = Color.White

val currentDayBackgroundColor: Color
    @Composable get() = Color.Black

/* --- Calendar week (week number column) --- */

val weekItemTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val weekItemBackgroundColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF3D4043) else Color(0xFFDBDCE0)