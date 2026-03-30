package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.model.DayCornerPosition
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import com.nmd.eventCalendar.compose.util.generateMonthDays
import java.time.YearMonth
import java.time.temporal.WeekFields

@Composable
fun MonthView(
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
            columns = GridCells.Fixed(if (calendarOptions.calendarWeekVisible) 8 else 7),
        ) {
            weeks.forEachIndexed { weekIndex, week ->
                if (calendarOptions.calendarWeekVisible) {
                    val weekNumber = week.first().date.get(WeekFields.ISO.weekOfWeekBasedYear())
                    val position = when (weekIndex) {
                        0 -> WeekItemPosition.Top
                        weeks.lastIndex -> WeekItemPosition.Bottom
                        else -> WeekItemPosition.Middle
                    }

                    item {
                        Box(
                            modifier = Modifier.height(itemHeight),
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

                itemsIndexed(week) { dayIndex, day ->
                    val corner = dayCornerFor(
                        row = weekIndex,
                        col = dayIndex,
                        lastRow = weeks.lastIndex
                    )

                    Box(
                        modifier = Modifier.height(itemHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        DayItem(
                            calendarDay = day,
                            corner = corner,
                            visibleMonth = yearMonth,
                            calendarStyle = calendarStyle
                        )
                    }
                }
            }
        }
    }
}

private fun dayCornerFor(
    row: Int,
    col: Int,
    lastRow: Int
): DayCornerPosition = when (row) {
    0 if col == 0 -> DayCornerPosition.TopLeft
    0 if col == 6 -> DayCornerPosition.TopRight
    lastRow if col == 0 -> DayCornerPosition.BottomLeft
    lastRow if col == 6 -> DayCornerPosition.BottomRight
    else -> DayCornerPosition.Default
}

@Preview(showBackground = true)
@Composable
fun MonthViewPreview() {
    MonthView(
        yearMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions(),
        calendarStyle = defaultCalendarStyle()
    )
}