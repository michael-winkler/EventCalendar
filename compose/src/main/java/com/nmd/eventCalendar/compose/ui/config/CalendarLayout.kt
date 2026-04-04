package com.nmd.eventCalendar.compose.ui.config

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Standard height for a calendar week row when in a fixed-height mode
 * (e.g., phone landscape or single week view).
 */
val CalendarRowHeight = 90.dp

/**
 * Determines the root modifier for the calendar screen based on the display mode.
 *
 * @param options Calendar configuration options.
 * @param isPhoneLandscape Whether the device is in phone landscape mode.
 */
fun Modifier.calendarRoot(options: CalendarOptions, isPhoneLandscape: Boolean): Modifier =
    if (options.isCurrentWeekOnly) {
        if (isPhoneLandscape) this
            .fillMaxWidth()
            .wrapContentHeight() else this.wrapContentHeight()
    } else {
        this.fillMaxSize()
    }

/**
 * Modifier for the month grid/pager container.
 * In scrollable or single-week modes, it wraps height; otherwise, it fills the screen.
 *
 * @param options Calendar configuration options.
 * @param isPhoneLandscape Whether the device is in phone landscape mode.
 */
fun Modifier.calendarMonthGrid(options: CalendarOptions, isPhoneLandscape: Boolean): Modifier =
    if (options.isCurrentWeekOnly || isPhoneLandscape) {
        this
            .fillMaxWidth()
            .wrapContentHeight()
    } else {
        this.fillMaxSize()
    }

/**
 * Modifier for a single week row within a Column.
 * Uses a fixed height for scrolling/single week, or weight(1f) to fill available space.
 *
 * @param columnScope The scope of the parent Column.
 * @param options Calendar configuration options.
 * @param isPhoneLandscape Whether the device is in phone landscape mode.
 */
fun Modifier.calendarRow(
    columnScope: ColumnScope,
    options: CalendarOptions,
    isPhoneLandscape: Boolean
): Modifier = with(columnScope) {
    if (options.isCurrentWeekOnly || isPhoneLandscape) {
        this@calendarRow
            .height(CalendarRowHeight)
            .fillMaxWidth()
    } else {
        this@calendarRow
            .weight(1f)
            .fillMaxWidth()
    }
}

/**
 * Modifier for the body section in CalendarScreen.
 * Handles the logic for vertical scrolling in landscape mode and height distribution.
 *
 * @param columnScope The scope of the parent Column.
 * @param options Calendar configuration options.
 * @param isPhoneLandscape Whether the device is in phone landscape mode.
 * @param scrollState The scroll state used for vertical scrolling in landscape mode.
 */
fun Modifier.calendarBody(
    columnScope: ColumnScope,
    options: CalendarOptions,
    isPhoneLandscape: Boolean,
    scrollState: ScrollState
): Modifier = with(columnScope) {
    if (options.isCurrentWeekOnly) {
        this@calendarBody
            .fillMaxWidth()
            .wrapContentHeight()
    } else {
        this@calendarBody
            .fillMaxWidth()
            .then(if (isPhoneLandscape) Modifier.fillMaxHeight() else Modifier.weight(1f))
            .then(if (isPhoneLandscape) Modifier.verticalScroll(scrollState) else Modifier)
    }
}
