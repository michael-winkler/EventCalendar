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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarWeekHeader(
    yearMonth: YearMonth,
    weekStart: DayOfWeek,
    calendarWeekVisible: Boolean,
    itemHeight: Dp,
    calendarStyle: CalendarStyle
) {
    val daysOfWeek = remember(weekStart) {
        (0 until 7).map { weekStart.plus(it.toLong()) }
    }

    val today = remember { LocalDate.now() }

    val isCurrentMonth = today.year == yearMonth.year &&
            today.month == yearMonth.month

    LazyVerticalGrid(
        columns = GridCells.Fixed(if (calendarWeekVisible) 8 else 7),
        userScrollEnabled = false
    ) {
        if (calendarWeekVisible) {
            item {
                Box(
                    modifier = Modifier.height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("KW")
                }
            }
        }

        items(daysOfWeek) { day ->

            val isToday = isCurrentMonth && day == today.dayOfWeek

            Box(
                modifier = Modifier.height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.getDisplayName(
                        TextStyle.SHORT_STANDALONE,
                        Locale.getDefault()
                    ),
                    color = if (isToday) calendarStyle.currentWeekDayTextColor else calendarStyle.defaultWeekDayTextColor,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}