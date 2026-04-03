package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.shapes.WeekItemShapes

private val sharedWeekItemShapes = WeekItemShapes()

/**
 * Displays a single calendar week number cell (KW).
 *
 * The shape can change depending on its position within the column (top/middle/bottom)
 * to create a continuous rounded background for the week-number column.
 *
 * @param modifier Modifier applied to the container.
 * @param weekNumber Week number to display.
 * @param position Position of the item within the week-number column (top/middle/bottom).
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 * @param isSingle If true, all corners are rounded (used for single-week view).
 */
@Composable
fun WeekItem(
    modifier: Modifier = Modifier,
    weekNumber: Int,
    position: WeekItemPosition,
    calendarStyle: CalendarStyle,
    isSingle: Boolean = false
) {
    val shape =
        remember(position, isSingle) { sharedWeekItemShapes.forPosition(position, isSingle) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .background(calendarStyle.weekItemBackgroundColor, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = weekNumber.toString(),
            color = calendarStyle.weekItemTextColor,
            fontSize = calendarStyle.textUnit
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeekItemPreview() {
    WeekItem(
        modifier = Modifier,
        weekNumber = 1,
        position = WeekItemPosition.Top,
        calendarStyle = defaultCalendarStyle()
    )
}