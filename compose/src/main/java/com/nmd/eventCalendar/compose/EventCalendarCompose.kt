package com.nmd.eventCalendar.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.ui.CalendarScreen
import com.nmd.eventCalendar.compose.ui.CalendarStyle
import com.nmd.eventCalendar.compose.ui.defaultCalendarStyle
import java.time.DayOfWeek

@Composable
fun EventCalendarCompose(
    modifier: Modifier = Modifier,
    weekStart: DayOfWeek = DayOfWeek.MONDAY,
    headerVisible: Boolean = true,
    calendarWeekVisible: Boolean = false,
    calendarStyle: CalendarStyle = defaultCalendarStyle()
) {
    CalendarScreen(
        modifier = modifier.fillMaxSize(),
        weekStart = weekStart,
        headerVisible = headerVisible,
        calendarWeekVisible = calendarWeekVisible,
        calendarStyle = calendarStyle
    )
}

@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    EventCalendarCompose()
}