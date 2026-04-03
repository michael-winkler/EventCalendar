package com.nmd.eventCalendar.compose

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
    calendarStyle: CalendarStyle = defaultCalendarStyle(),
    calendarOptions: CalendarOptions = defaultCalendarOptions(),
    calendarController: CalendarController = rememberCalendarController(calendarOptions),
    calendarEventsStore: CalendarEventsStore,
    onDaySelected: (calendarDay: CalendarDay) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    CalendarScreen(
        modifier = modifier,
        calendarStyle = calendarStyle,
        calendarOptions = calendarOptions,
        calendarController = calendarController,
        calendarEventsStore = calendarEventsStore,
        onDaySelected = onDaySelected,
        onMonthChange = onMonthChange
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
        calendarEventsStore = store,
        onDaySelected = {},
        onMonthChange = {}
    )
}