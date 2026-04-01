package com.nmd.eventCalendar.compose.ui.events

import com.nmd.eventCalendar.compose.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

/**
 * Simple in-memory [CalendarEventsStore] intended for previews and tests.
 *
 * Stores a pre-grouped + sorted map in a StateFlow so Compose can collect it efficiently.
 */
internal class PreviewCalendarEventsStore(
    initialEvents: List<Event>
) : CalendarEventsStore {

    private val _eventsByDateFlow = MutableStateFlow(buildEventsByDate(initialEvents))
    override val eventsByDateFlow: StateFlow<Map<LocalDate, List<Event>>> =
        _eventsByDateFlow.asStateFlow()

    override fun setEvents(events: List<Event>) {
        _eventsByDateFlow.value = buildEventsByDate(events)
    }

    private fun buildEventsByDate(events: List<Event>): Map<LocalDate, List<Event>> {
        if (events.isEmpty()) return emptyMap()

        return events
            .groupBy { it.date }
            .mapValues { (_, list) ->
                list.sortedWith(
                    compareBy<Event> { it.timeRange?.startHour ?: Int.MAX_VALUE }
                        .thenBy { it.timeRange?.startMinute ?: Int.MAX_VALUE }
                        .thenBy { it.name }
                )
            }
    }
}