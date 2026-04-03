package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.util.generateMonthDays
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields

/**
 * Displays the calendar week numbers (KW) as a vertical column aligned with the month grid.
 *
 * In phone landscape mode, each row uses a fixed height ([PhoneLandscapeRowHeight]) so the column
 * can participate in a shared, parent-driven vertical scroll together with the month grid.
 *
 * @param modifier Modifier applied to the column container.
 * @param yearMonth The month used to calculate week numbers for the displayed 6-week grid.
 * @param weekStart First day of week (e.g., Monday).
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 * @param phoneLandscape If true, uses fixed row heights optimized for phone landscape layouts.
 * @param isCurrentWeekOnly If true, only the current week number is displayed.
 */
@Composable
fun WeekNumberColumn(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    weekStart: DayOfWeek,
    calendarStyle: CalendarStyle,
    phoneLandscape: Boolean = false,
    isCurrentWeekOnly: Boolean = false
) {
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
                    .then(
                        when {
                            isCurrentWeekOnly -> Modifier.height(90.dp)
                            phoneLandscape -> Modifier.height(PhoneLandscapeRowHeight)
                            else -> Modifier.weight(1f)
                        }
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
        phoneLandscape = true
    )
}