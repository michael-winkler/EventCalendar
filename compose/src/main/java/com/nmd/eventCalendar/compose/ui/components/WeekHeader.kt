package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventcalendar.compose.R
import java.time.YearMonth
import java.time.format.TextStyle

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
 */
@Composable
fun WeekHeader(
    currentMonth: YearMonth,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle
) {
    val daysOfWeek = remember(calendarOptions.weekStart) {
        (0 until 7).map { calendarOptions.weekStart.plus(it.toLong()) }
    }

    val isCurrentMonth = remember(currentMonth) {
        today.year == currentMonth.year && today.month == currentMonth.month
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        if (calendarOptions.calendarWeekVisible) {
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 2.dp),
                    text = stringResource(R.string.calendar_week_label),
                    color = calendarStyle.defaultWeekDayTextColor,
                    fontSize = calendarStyle.textUnit,
                    lineHeight = calendarStyle.textUnit
                )
            }
        }

        daysOfWeek.forEach { day ->
            val isToday = isCurrentMonth && (day == today.dayOfWeek)

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 2.dp),
                    text = day.getDisplayName(
                        TextStyle.SHORT_STANDALONE,
                        LocalLocale.current.platformLocale
                    ),
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

@Preview(showBackground = true)
@Composable
fun WeekHeaderPreview() {
    WeekHeader(
        currentMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions(),
        calendarStyle = defaultCalendarStyle()
    )
}