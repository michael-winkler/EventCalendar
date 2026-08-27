package com.nmd.eventCalendar.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmd.eventCalendar.compose.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * Holds the current list of calendar [Event]s.
 *
 * Notes:
 * - The source of truth is a [StateFlow] of raw events.
 * - [eventsByDateFlow] is derived from raw events by grouping and sorting.
 * - [stateIn] caches the latest computed value and only keeps upstream active while subscribed.
 */
class CalendarEventsViewModel : ViewModel() {

    private val _rawEvents = MutableStateFlow<List<Event>>(emptyList())
    val rawEvents: StateFlow<List<Event>> = _rawEvents.asStateFlow()

    val eventsByDateFlow: StateFlow<Map<LocalDate, List<Event>>> =
        _rawEvents
            .map { events -> buildEventsByDate(events) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = emptyMap()
            )

    fun setEvents(events: List<Event>) {
        _rawEvents.value = events
    }

    private fun buildEventsByDate(events: List<Event>): Map<LocalDate, List<Event>> {
        if (events.isEmpty()) return emptyMap()

        // Multi-day events are expanded across every day they cover so that a per-date lookup
        // (e.g. eventsByDate[date]) returns all events overlapping that day, not just the ones
        // that start on it.
        val grouped = LinkedHashMap<LocalDate, MutableList<Event>>()
        for (event in events) {
            var day = event.date
            val last = event.lastDate
            while (day <= last) {
                grouped.getOrPut(day) { mutableListOf() }.add(event)
                day = day.plus(1, DateTimeUnit.DAY)
            }
        }

        return grouped.mapValues { (_, list) ->
            list.sortedWith(
                compareBy<Event> { it.timeRange?.startHour ?: Int.MAX_VALUE }
                    .thenBy { it.timeRange?.startMinute ?: Int.MAX_VALUE }
                    .thenBy { it.name }
            )
        }
    }
}