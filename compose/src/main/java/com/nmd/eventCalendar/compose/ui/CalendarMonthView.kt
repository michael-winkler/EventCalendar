package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.util.generateMonthDays
import java.time.YearMonth
import java.time.temporal.WeekFields

@Composable
fun CalendarMonthView(
    yearMonth: YearMonth,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle
) {
    val days = remember(yearMonth, calendarOptions.weekStart) {
        generateMonthDays(yearMonth, calendarOptions.weekStart)
    }
    val weeks = remember(days) { days.chunked(7) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val itemHeight = maxHeight / 6

        LazyVerticalGrid(
            columns = GridCells.Fixed(if (calendarOptions.calendarWeekVisible) 8 else 7)
        ) {
            weeks.forEach { week ->
                if (calendarOptions.calendarWeekVisible) {
                    val weekNumber = week.first().date.get(WeekFields.ISO.weekOfWeekBasedYear())
                    item {
                        Box(
                            modifier = Modifier.height(itemHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            CalendarWeekItem(
                                modifier = Modifier,
                                weekNumber = weekNumber,
                                calendarStyle = calendarStyle
                            )
                        }
                    }
                }

                items(week) { day ->
                    Box(
                        modifier = Modifier.height(itemHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        DayItem(
                            calendarDay = day,
                            calendarStyle = calendarStyle
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarMonthViewPreview() {
    CalendarMonthView(
        yearMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions(),
        calendarStyle = defaultCalendarStyle()
    )
}