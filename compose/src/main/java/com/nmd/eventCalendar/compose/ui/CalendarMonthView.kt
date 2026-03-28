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
import com.nmd.eventCalendar.compose.util.generateMonthDays
import java.time.DayOfWeek
import java.time.YearMonth

@Composable
fun CalendarMonthView(
    yearMonth: YearMonth,
    weekStart: DayOfWeek
) {
    val days = remember(yearMonth, weekStart) {
        generateMonthDays(yearMonth, weekStart)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val itemHeight = maxHeight / 6

        LazyVerticalGrid(
            columns = GridCells.Fixed(7)
        ) {
            items(days) { day ->
                Box(
                    modifier = Modifier.height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    DayItem(day)
                }
            }
        }
    }
}