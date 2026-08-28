package com.nmd.eventCalendar.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.util.sortedForDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Holds the current list of calendar [Event]s.
 *
 * Notes:
 * - The source of truth is a [StateFlow] of raw events.
 * - [eventsFlow] is derived from raw events by sorting into a stable display order.
 * - [stateIn] caches the latest computed value and only keeps upstream active while subscribed.
 */
class CalendarEventsViewModel : ViewModel() {

    private val _rawEvents = MutableStateFlow<List<Event>>(emptyList())
    val rawEvents: StateFlow<List<Event>> = _rawEvents.asStateFlow()

    val eventsFlow: StateFlow<List<Event>> =
        _rawEvents
            .map { events -> events.sortedForDisplay() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = emptyList()
            )

    fun setEvents(events: List<Event>) {
        _rawEvents.value = events
    }
}
