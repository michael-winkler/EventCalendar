package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay

@Composable
fun DayItem(
    calendarDay: CalendarDay,
    calendarStyle: CalendarStyle
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            modifier = Modifier.padding(vertical = 2.dp),
            text = calendarDay.date.dayOfMonth.toString(),
            color = if (calendarDay.isCurrentMonth) calendarStyle.weekDayTextColor else calendarStyle.weekDayInactiveTextColor,
            fontStyle = if (calendarDay.isCurrentMonth) FontStyle.Normal else FontStyle.Italic
        )
    }
}