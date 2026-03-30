package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.nmd.eventcalendar.compose.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekHeader(
    currentMonth: YearMonth,
    itemHeight: Dp,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle
) {
    val daysOfWeek = remember(calendarOptions.weekStart) {
        (0 until 7).map { calendarOptions.weekStart.plus(it.toLong()) }
    }

    val today = remember { LocalDate.now() }

    val isCurrentMonth = today.year == currentMonth.year &&
            today.month == currentMonth.month

    LazyVerticalGrid(
        columns = GridCells.Fixed(if (calendarOptions.calendarWeekVisible) 8 else 7),
        userScrollEnabled = false
    ) {
        if (calendarOptions.calendarWeekVisible) {
            item {
                Box(
                    modifier = Modifier.height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.calendar_week_label),
                        color = calendarStyle.defaultWeekDayTextColor,
                        fontSize = calendarStyle.fontsize
                    )
                }
            }
        }

        items(daysOfWeek) { day ->
            val isToday = isCurrentMonth && (day == today.dayOfWeek)

            Box(
                modifier = Modifier.height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()),
                    color = if (isToday) calendarStyle.currentWeekDayTextColor
                    else calendarStyle.defaultWeekDayTextColor,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    fontSize = calendarStyle.fontsize
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
        itemHeight = Dp.Unspecified,
        calendarOptions = defaultCalendarOptions(),
        calendarStyle = defaultCalendarStyle()
    )
}