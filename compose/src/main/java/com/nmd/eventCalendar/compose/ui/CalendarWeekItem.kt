package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CalendarWeekItem(
    modifier: Modifier = Modifier,
    weekNumber: Int,
    calendarStyle: CalendarStyle
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = weekNumber.toString(),
            color = calendarStyle.weekLabelColor
        )
    }
}