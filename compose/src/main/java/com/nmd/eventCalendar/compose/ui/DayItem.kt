package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nmd.eventCalendar.compose.model.CalendarDay

@Composable
fun DayItem(
    calendarDay: CalendarDay,
    calendarStyle: CalendarStyle // TODO: Add colors to style
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = calendarDay.date.dayOfMonth.toString(),
            color = if (calendarDay.isCurrentMonth) Color.Black else Color.Gray
        )
    }
}