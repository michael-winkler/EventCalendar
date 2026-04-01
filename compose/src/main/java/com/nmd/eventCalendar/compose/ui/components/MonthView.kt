package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.DayCornerPosition
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.util.generateMonthDays
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

@Composable
fun MonthView(
    yearMonth: YearMonth,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    eventsForDate: (LocalDate) -> List<Event>,
    onDaySelected: (calendarDay: CalendarDay) -> Unit
) {
    val baseDays = remember(yearMonth, calendarOptions.weekStart) {
        generateMonthDays(
            yearMonth = yearMonth,
            weekStart = calendarOptions.weekStart,
            eventsByDate = emptyMap()
        )
    }

    val days = remember(baseDays, eventsForDate) {
        baseDays.map { day -> day.copy(events = eventsForDate(day.date)) }
    }

    val weeks = remember(days) { days.chunked(7) }

    val weekNumbers = remember(weeks, calendarOptions.calendarWeekVisible) {
        if (!calendarOptions.calendarWeekVisible) emptyList()
        else weeks.map { week -> week.first().date.get(WeekFields.ISO.weekOfWeekBasedYear()) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val constraintsScope = this
        val rows = 6
        val cellHeight = constraintsScope.maxHeight / rows
        val columns = if (calendarOptions.calendarWeekVisible) 8 else 7
        val cellWidth = constraintsScope.maxWidth / columns

        Column(Modifier.fillMaxSize()) {
            weeks.forEachIndexed { weekIndex, week ->
                Row(Modifier.height(cellHeight)) {
                    if (calendarOptions.calendarWeekVisible) {
                        val weekNumber = weekNumbers[weekIndex]
                        val position = when (weekIndex) {
                            0 -> WeekItemPosition.Top
                            weeks.lastIndex -> WeekItemPosition.Bottom
                            else -> WeekItemPosition.Middle
                        }
                        Box(
                            modifier = Modifier
                                .width(cellWidth)
                                .height(cellHeight),
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
                    week.forEachIndexed { dayIndex, day ->
                        val corner = dayCornerFor(
                            row = weekIndex,
                            col = dayIndex,
                            lastRow = weeks.lastIndex
                        )
                        Box(
                            modifier = Modifier
                                .width(cellWidth)
                                .height(cellHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            DayItem(
                                calendarDay = day,
                                corner = corner,
                                visibleMonth = yearMonth,
                                calendarStyle = calendarStyle,
                                onDaySelected = onDaySelected
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun dayCornerFor(row: Int, col: Int, lastRow: Int): DayCornerPosition = when (row) {
    0 if col == 0 -> DayCornerPosition.TopLeft
    0 if col == 6 -> DayCornerPosition.TopRight
    lastRow if col == 0 -> DayCornerPosition.BottomLeft
    lastRow if col == 6 -> DayCornerPosition.BottomRight
    else -> DayCornerPosition.Default
}

@Preview(showBackground = true)
@Composable
fun MonthViewPreview() {
    val previewToday = LocalDate.now()
    MonthView(
        yearMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle(),
        eventsForDate = { date ->
            if (date == previewToday) {
                listOf(
                    Event(
                        previewToday,
                        "Cooking",
                        shapeColor = Color(0xFFEF6C00),
                        textColor = Color.White
                    ),
                    Event(
                        previewToday,
                        "Board Games",
                        shapeColor = Color(0xFF43A047),
                        textColor = Color.White
                    ),
                    Event(
                        previewToday,
                        "Volunteer",
                        shapeColor = Color(0xFF3949AB),
                        textColor = Color.White
                    ),
                    Event(
                        previewToday,
                        "Movie Night",
                        shapeColor = Color(0xFFFDD835),
                        textColor = Color.Black
                    ),
                    Event(
                        previewToday,
                        "Vacation",
                        shapeColor = Color(0xFF039BE5),
                        textColor = Color.White
                    ),
                )
            } else emptyList()
        },
        onDaySelected = {}
    )
}