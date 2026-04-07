package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.calendarRow
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.util.generateMonthDays
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields

/**
 * Displays the calendar week numbers (KW) as a vertical column aligned with the month grid.
 *
 * In phone landscape mode, each row uses a fixed height ([com.nmd.eventCalendar.compose.ui.config.CalendarRowHeight]) so the column
 * can participate in a shared, parent-driven vertical scroll together with the month grid.
 *
 * If [CalendarOptions.isCurrentWeekOnly] is enabled, only a single week number is displayed.
 *
 * @param modifier Modifier applied to the column container.
 * @param yearMonth The month used to calculate week numbers (ignored if [calendarOptions.isCurrentWeekOnly] is true).
 * @param weekStart First day of week (e.g., Monday).
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 * @param calendarOptions Configuration options for the calendar.
 * @param phoneLandscape If true, uses fixed row heights optimized for phone landscape layouts.
 */
@Composable
internal fun WeekNumberColumn(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    weekStart: DayOfWeek,
    calendarStyle: CalendarStyle,
    calendarOptions: CalendarOptions,
    phoneLandscape: Boolean = false
) {
    val isCurrentWeekOnly = calendarOptions.isCurrentWeekOnly
    val weekNumbers = remember(yearMonth, weekStart, isCurrentWeekOnly) {
        val days = generateMonthDays(
            yearMonth = yearMonth,
            weekStart = weekStart,
            eventsByDate = emptyMap(),
            isCurrentWeekOnly = isCurrentWeekOnly
        )
        val weeks = days.chunked(7)
        weeks.map { week -> week.first().date.get(WeekFields.ISO.weekOfWeekBasedYear()) }
    }

    Column(modifier = modifier) {
        weekNumbers.forEachIndexed { index, weekNumber ->
            val position = when {
                weekNumbers.size == 1 -> WeekItemPosition.Middle
                index == 0 -> WeekItemPosition.Top
                index == weekNumbers.lastIndex -> WeekItemPosition.Bottom
                else -> WeekItemPosition.Middle
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .calendarRow(
                        columnScope = this,
                        options = calendarOptions,
                        isPhoneLandscape = phoneLandscape
                    ),
                contentAlignment = Alignment.Center
            ) {
                WeekItem(
                    modifier = Modifier.fillMaxSize(),
                    weekNumber = weekNumber,
                    position = position,
                    calendarStyle = calendarStyle,
                    isSingle = isCurrentWeekOnly
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 80, heightDp = 520)
@Composable
fun WeekNumberColumnPreview() {
    WeekNumberColumn(
        modifier = Modifier,
        yearMonth = YearMonth.now(),
        weekStart = DayOfWeek.MONDAY,
        calendarStyle = defaultCalendarStyle(),
        calendarOptions = defaultCalendarOptions(),
        phoneLandscape = true
    )
}
