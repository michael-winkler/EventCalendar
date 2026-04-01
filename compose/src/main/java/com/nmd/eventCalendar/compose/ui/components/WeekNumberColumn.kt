package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.util.generateMonthDays
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields

@Composable
fun WeekNumberColumn(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    weekStart: DayOfWeek,
    calendarStyle: com.nmd.eventCalendar.compose.ui.config.CalendarStyle
) {
    val weekNumbers = remember(yearMonth, weekStart) {
        val days = generateMonthDays(
            yearMonth = yearMonth,
            weekStart = weekStart,
            eventsByDate = emptyMap()
        )
        val weeks = days.chunked(7)
        weeks.map { week -> week.first().date.get(WeekFields.ISO.weekOfWeekBasedYear()) }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
    ) {
        weekNumbers.forEachIndexed { index, weekNumber ->
            val position = when (index) {
                0 -> WeekItemPosition.Top
                weekNumbers.lastIndex -> WeekItemPosition.Bottom
                else -> WeekItemPosition.Middle
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                WeekItem(
                    modifier = Modifier.fillMaxSize(),
                    weekNumber = weekNumber,
                    position = position,
                    calendarStyle = calendarStyle
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 80, heightDp = 320)
@Composable
fun WeekNumberColumnPreview() {
    WeekNumberColumn(
        modifier = Modifier.fillMaxHeight(),
        yearMonth = YearMonth.now(),
        weekStart = DayOfWeek.MONDAY,
        calendarStyle = defaultCalendarStyle()
    )
}