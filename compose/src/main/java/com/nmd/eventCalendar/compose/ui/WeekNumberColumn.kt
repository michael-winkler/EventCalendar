package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields

@Composable
fun WeekNumberColumn(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    weekStart: DayOfWeek,
    calendarStyle: CalendarStyle
) {
    val weekNumbers = remember(yearMonth, weekStart) {
        val days = com.nmd.eventCalendar.compose.util.generateMonthDays(
            yearMonth = yearMonth,
            weekStart = weekStart,
            eventsByDate = emptyMap()
        )
        val weeks = days.chunked(7)
        weeks.map { week ->
            week.first().date.get(WeekFields.ISO.weekOfWeekBasedYear())
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {
        val constraintsScope = this
        val rows = 6
        val cellHeight = constraintsScope.maxHeight / rows

        Column(Modifier.fillMaxSize()) {
            weekNumbers.forEachIndexed { index, weekNumber ->
                val position = when (index) {
                    0 -> WeekItemPosition.Top
                    weekNumbers.lastIndex -> WeekItemPosition.Bottom
                    else -> WeekItemPosition.Middle
                }

                Box(
                    modifier = Modifier.height(cellHeight),
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
}