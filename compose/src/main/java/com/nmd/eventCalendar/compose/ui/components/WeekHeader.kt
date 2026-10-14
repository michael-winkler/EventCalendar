package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.CalendarWeekColumnWidth
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.util.calendarWeekLabel
import com.nmd.eventCalendar.compose.util.dayOfWeekShortName
import com.nmd.eventCalendar.compose.util.plus
import com.nmd.eventCalendar.compose.util.rememberToday

/**
 * Displays the weekday header row for the calendar grid.
 *
 * Optionally renders a leading "calendar week" (KW) label cell when
 * [CalendarOptions.calendarWeekVisible] is enabled.
 *
 * The current weekday (if the visible month is the device's current month) is highlighted.
 *
 * @param currentMonth The month currently displayed.
 * @param calendarOptions Calendar configuration options (week start, week number visibility).
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 * @param phoneLandscape If true, uses fixed widths for the week number column to align with the grid.
 */
@Composable
internal fun WeekHeader(
    currentMonth: YearMonth,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    phoneLandscape: Boolean = false
) {
    val today = rememberToday()

    val daysOfWeek = remember(calendarOptions.weekStart) {
        (0 until 7).map { calendarOptions.weekStart.plus(it.toLong()) }
    }

    val isCurrentMonth = remember(currentMonth, today) {
        today.year == currentMonth.year && today.month == currentMonth.month
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        if (calendarOptions.calendarWeekVisible) {
            Box(
                modifier = Modifier
                    .then(
                        if (phoneLandscape) Modifier.width(CalendarWeekColumnWidth)
                        else Modifier.weight(1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 2.dp),
                    text = calendarWeekLabel(),
                    color = calendarStyle.defaultWeekDayTextColor,
                    fontSize = calendarStyle.textUnit,
                    lineHeight = calendarStyle.textUnit
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(if (phoneLandscape) 1f else 7f)
        ) {
            daysOfWeek.forEach { day ->
                val isToday = isCurrentMonth && (day == today.dayOfWeek)

                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 2.dp),
                        text = dayOfWeekShortName(day),
                        color = if (isToday) {
                            calendarStyle.currentWeekDayTextColor
                        } else {
                            calendarStyle.defaultWeekDayTextColor
                        },
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        fontSize = calendarStyle.textUnit,
                        lineHeight = calendarStyle.textUnit
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun WeekHeaderPreview() {
    WeekHeader(
        currentMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle()
    )
}

@Preview(showBackground = true, widthDp = 740)
@Composable
internal fun WeekHeaderPreviewLandscape() {
    WeekHeader(
        currentMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle(),
        phoneLandscape = true
    )
}
