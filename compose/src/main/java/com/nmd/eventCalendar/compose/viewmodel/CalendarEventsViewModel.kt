package com.nmd.eventCalendar.compose.viewmodel

import androidx.lifecycle.ViewModel
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.util.sortedForDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the current list of calendar [Event]s.
 *
 * Notes:
 * - The source of truth is a [StateFlow] of raw events.
 * - [eventsFlow] holds the same events in a stable display order. It is updated synchronously in
 *   [setEvents], so its `value` is current right after the call (with or without collectors) and
 *   a calendar shows the events on its very first frame.
 */
class CalendarEventsViewModel : ViewModel() {

    private val _rawEvents = MutableStateFlow<List<Event>>(emptyList())
    val rawEvents: StateFlow<List<Event>> = _rawEvents.asStateFlow()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val eventsFlow: StateFlow<List<Event>> = _events.asStateFlow()

    fun setEvents(events: List<Event>) {
        _rawEvents.value = events
        _events.value = events.sortedForDisplay()
    }
}
