package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

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

@Preview(showBackground = true)
@Composable
fun CalendarWeekItemPreview() {
    CalendarWeekItem(
        modifier = Modifier.fillMaxSize(),
        weekNumber = 1,
        calendarStyle = defaultCalendarStyle()
    )
}