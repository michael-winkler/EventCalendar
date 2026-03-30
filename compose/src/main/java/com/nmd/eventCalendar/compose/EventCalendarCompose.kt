package com.nmd.eventCalendar.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.ui.CalendarOptions
import com.nmd.eventCalendar.compose.ui.CalendarScreen
import com.nmd.eventCalendar.compose.ui.CalendarStyle
import com.nmd.eventCalendar.compose.ui.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.defaultCalendarStyle

@Composable
fun EventCalendarCompose(
    modifier: Modifier = Modifier,
    onDaySelected: (calendarDay: CalendarDay) -> Unit,
    calendarOptions: CalendarOptions = defaultCalendarOptions(),
    calendarStyle: CalendarStyle = defaultCalendarStyle()
) {
    CalendarScreen(
        modifier = modifier.fillMaxSize(),
        onDaySelected = onDaySelected,
        calendarOptions = calendarOptions,
        calendarStyle = calendarStyle
    )
}

@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    EventCalendarCompose(onDaySelected = {})
}