package com.nmd.eventCalendar.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventCalendar.compose.ui.CalendarScreen
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.controller.CalendarController
import com.nmd.eventCalendar.compose.ui.controller.rememberCalendarController
import com.nmd.eventCalendar.compose.ui.events.CalendarEventsStore
import com.nmd.eventCalendar.compose.ui.events.PreviewCalendarEventsStore
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * The main entry point for the Event Calendar Compose library.
 *
 * This composable initializes the calendar UI, including the month navigation,
 * weekday headers, and the day grid. It manages the internal state via a [CalendarController]
 * and observes events from a [CalendarEventsStore].
 *
 * @param modifier Modifier applied to the root layout of the calendar.
 * @param calendarStyle Custom styling for colors and typography. Defaults to [defaultCalendarStyle].
 * @param calendarOptions Functional configuration (e.g., week start, bounds). Defaults to [defaultCalendarOptions].
 * @param calendarController Controller for programmatic navigation and state management.
 * @param calendarEventsStore Store providing the events to be displayed on the calendar.
 * @param onDaySelected Callback triggered when a user clicks on a specific day.
 * @param onMonthChange Callback triggered when the displayed month changes (via swiping or navigation).
 */
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
internal fun EventCalendarComposePreview() {
    val today = kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())

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