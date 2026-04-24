package com.nmd.eventCalendar.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.EventTimeRange
import com.nmd.eventCalendar.compose.ui.components.TimeGridView
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.events.CalendarEventsStore
import com.nmd.eventCalendar.compose.ui.events.PreviewCalendarEventsStore
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * A time-based week/day calendar view entry point.
 *
 * This view displays a time grid where events are positioned vertically according to their
 * start and end times. It supports displaying 1, 3, or 7 consecutive days.
 *
 * @param modifier Modifier applied to the root layout.
 * @param calendarStyle Custom styling for colors and typography.
 * @param calendarOptions Functional configuration (e.g., week start, number of visible days).
 * @param calendarEventsStore Store providing the events to be displayed.
 * @param onDaySelected Callback triggered when a day (background) is selected.
 * @param onEventSelected Callback triggered when a specific event is selected.
 */
@Composable
fun EventCalendarWeekTime(
    modifier: Modifier = Modifier,
    calendarStyle: CalendarStyle = defaultCalendarStyle(),
    calendarOptions: CalendarOptions = defaultCalendarOptions(),
    calendarEventsStore: CalendarEventsStore,
    onDaySelected: (LocalDate) -> Unit = {},
    onEventSelected: (Event) -> Unit = {}
) {
    val eventsByDate by calendarEventsStore.eventsByDateFlow.collectAsStateWithLifecycle()
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    val days = remember(calendarOptions.weekStart, calendarOptions.noOfVisibleDays) {
        val daysUntil = (today.dayOfWeek.ordinal - calendarOptions.weekStart.ordinal + 7) % 7
        val startOfWeek = today.minus(daysUntil, DateTimeUnit.DAY)
        (0 until calendarOptions.noOfVisibleDays).map { startOfWeek.plus(it, DateTimeUnit.DAY) }
    }

    TimeGridView(
        modifier = modifier,
        days = days,
        eventsByDate = eventsByDate,
        calendarStyle = calendarStyle,
        onDaySelected = onDaySelected,
        onEventSelected = onEventSelected
    )
}

@Preview(showBackground = true)
@Composable
internal fun EventCalendarWeekTimePreview() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val store = remember {
        PreviewCalendarEventsStore(
            initialEvents = listOf(
                Event(
                    today,
                    "Meeting",
                    shapeColor = Color.Blue,
                    textColor = Color.White,
                    timeRange = EventTimeRange(9, 0, 10, 30)
                ),
                Event(
                    today,
                    "Workshop",
                    shapeColor = Color.Red,
                    textColor = Color.White,
                    timeRange = EventTimeRange(10, 0, 12, 0)
                ),
                Event(
                    today.plus(1, DateTimeUnit.DAY),
                    "Lunch",
                    shapeColor = Color.Green,
                    textColor = Color.Black,
                    timeRange = EventTimeRange(12, 0, 13, 0)
                )
            )
        )
    }

    EventCalendarWeekTime(
        calendarEventsStore = store,
        calendarOptions = defaultCalendarOptions().copy(noOfVisibleDays = 3)
    )
}