package com.nmd.eventCalendar.compose.ui.events

import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.util.sortedForDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple in-memory [CalendarEventsStore] intended for previews and tests.
 *
 * Stores a stably ordered list in a StateFlow so Compose can collect it efficiently.
 */
internal class PreviewCalendarEventsStore(
    initialEvents: List<Event>
) : CalendarEventsStore {

    private val _eventsFlow = MutableStateFlow(initialEvents.sortedForDisplay())
    override val eventsFlow: StateFlow<List<Event>> = _eventsFlow.asStateFlow()

    override fun setEvents(events: List<Event>) {
        _eventsFlow.value = events.sortedForDisplay()
    }
}
