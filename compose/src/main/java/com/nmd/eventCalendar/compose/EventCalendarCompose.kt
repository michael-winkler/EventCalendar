package com.nmd.eventCalendar.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.ui.CalendarScreen
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.controller.CalendarController
import com.nmd.eventCalendar.compose.ui.controller.rememberCalendarController
import com.nmd.eventCalendar.compose.ui.events.CalendarEventsStore
import com.nmd.eventCalendar.compose.ui.events.PreviewCalendarEventsStore
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun EventCalendarCompose(
    modifier: Modifier = Modifier,
    calendarOptions: CalendarOptions = defaultCalendarOptions(),
    calendarStyle: CalendarStyle = defaultCalendarStyle(),
    calendarController: CalendarController = rememberCalendarController(calendarOptions),
    eventsStore: CalendarEventsStore,
    onDaySelected: (calendarDay: CalendarDay) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    CalendarScreen(
        modifier = modifier.fillMaxSize(),
        calendarController = calendarController,
        eventsStore = eventsStore,
        onDaySelected = onDaySelected,
        onMonthChange = onMonthChange,
        calendarOptions = calendarOptions,
        calendarStyle = calendarStyle,
    )
}

@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    val today = LocalDate.now()

    val store = remember {
        PreviewCalendarEventsStore(
            initialEvents = listOf(
                Event(today, "Cooking", shapeColor = Color(0xFFEF6C00), textColor = Color.White),
                Event(
                    today,
                    "Board Games",
                    shapeColor = Color(0xFF43A047),
                    textColor = Color.White
                ),
            )
        )
    }

    EventCalendarCompose(
        onDaySelected = {},
        onMonthChange = {},
        eventsStore = store
    )
}