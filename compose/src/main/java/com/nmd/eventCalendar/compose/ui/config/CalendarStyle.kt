package com.nmd.eventCalendar.compose.ui.config

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Styling configuration for the calendar UI.
 *
 * This is a pure configuration holder used to control colors and typography for the calendar.
 * You can either use [defaultCalendarStyle] as a baseline or provide your own [CalendarStyle]
 * instance to fully customize the look.
 *
 * All properties are interpreted by the calendar components; the meaning of each value is
 * documented at the property level.
 *
 * @property textUnit Default text size used across the calendar (e.g. day numbers, weekday labels).
 * @property monthNameTextUnit Text size used for the month title in the header.
 *
 * @property monthNavigationIconColor Tint color for month navigation icons (previous/next).
 * @property monthNameTextColor Text color for the month title.
 *
 * @property currentWeekDayTextColor Text color used to highlight the current weekday label in the header.
 * @property defaultWeekDayTextColor Default text color for weekday labels in the header.
 * @property weekDayTextColor Text color for weekday labels (Mon - Sun) representing days of the current month.
 * @property weekDayInactiveTextColor Text color for day numbers that belong to the previous/next month.
 *
 * @property dayItemTextColor Text color for day numbers belonging to the currently displayed month.
 * @property dayItemBackgroundColor Background color for day cells.
 * @property currentDayTextColor Text color used for the "today" indicator/badge.
 * @property currentDayBackgroundColor Background color used for the "today" indicator/badge.
 *
 * @property weekItemTextColor Text color used in the calendar-week (week number) column.
 * @property weekItemBackgroundColor Background color used in the calendar-week (week number) column.
 */
@Immutable
data class CalendarStyle(
    val textUnit: TextUnit,
    val monthNameTextUnit: TextUnit,

    // --- Month header / navigation ---
    val monthNavigationIconColor: Color,
    val monthNameTextColor: Color,

    // --- Weekday row (Mon..Sun) ---
    val currentWeekDayTextColor: Color,
    val defaultWeekDayTextColor: Color,
    val weekDayTextColor: Color,
    val weekDayInactiveTextColor: Color,

    // --- Day cells ---
    val dayItemTextColor: Color,
    val dayItemBackgroundColor: Color,
    val currentDayTextColor: Color,
    val currentDayBackgroundColor: Color,

    // --- Calendar week (week number column) ---
    val weekItemTextColor: Color,
    val weekItemBackgroundColor: Color
)

/**
 * Creates the default [CalendarStyle] based on the current system theme (light/dark).
 *
 * The returned style uses theme-aware colors (via [isSystemInDarkTheme]) and a reasonable
 * default typography setup.
 *
 * @return A theme-aware default [CalendarStyle].
 */
@Composable
fun defaultCalendarStyle(): CalendarStyle {
    return CalendarStyle(
        textUnit = 14.sp,
        monthNameTextUnit = 14.sp,

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

/** Tint color for month navigation icons (previous/next), theme-aware. */
val monthNavigationIconColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/** Text color for the month title, theme-aware. */
val monthNameTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/* --- Weekday row (Mon..Sun) --- */

/** Text color used to highlight the current weekday label, theme-aware. */
val currentWeekDayTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.Green else Color.Blue

/** Default text color for weekday labels, theme-aware. */
val defaultWeekDayTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/** Weekday label color for days that belong to the currently displayed month, theme-aware. */
val weekDayTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/** Label color for days that belong to the previous/next month (inactive), theme-aware. */
val weekDayInactiveTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.LightGray else Color.Gray

/* --- Day cells --- */

/** Default text color for day numbers, theme-aware. */
val dayItemTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/** Background color for day cells, theme-aware. */
val dayItemBackgroundColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF282A2C) else Color(0xFFF4F4F4)

/** Text color used for the "today" indicator/badge. */
val currentDayTextColor: Color
    @Composable get() = Color.White

/** Background color used for the "today" indicator/badge. */
val currentDayBackgroundColor: Color
    @Composable get() = Color.Black

/* --- Calendar week (week number column) --- */

/** Text color for week numbers, theme-aware. */
val weekItemTextColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

/** Background color for week numbers, theme-aware. */
val weekItemBackgroundColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF3D4043) else Color(0xFFDBDCE0)