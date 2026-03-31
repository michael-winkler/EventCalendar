package com.nmd.eventCalendar.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.ui.CalendarController
import com.nmd.eventCalendar.compose.ui.CalendarOptions
import com.nmd.eventCalendar.compose.ui.CalendarScreen
import com.nmd.eventCalendar.compose.ui.CalendarStyle
import com.nmd.eventCalendar.compose.ui.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.rememberCalendarController

@Composable
fun EventCalendarCompose(
    modifier: Modifier = Modifier,
    calendarController: CalendarController = rememberCalendarController(),
    onDaySelected: (calendarDay: CalendarDay) -> Unit,
    events: List<Event> = emptyList(),
    calendarOptions: CalendarOptions = defaultCalendarOptions(),
    calendarStyle: CalendarStyle = defaultCalendarStyle()
) {
    CalendarScreen(
        modifier = modifier.fillMaxSize(),
        calendarController = calendarController,
        calendarOptions = calendarOptions,
        calendarStyle = calendarStyle,
        events = events,
        onDaySelected = onDaySelected,
    )
}

@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    EventCalendarCompose(
        onDaySelected = {}
    )
}