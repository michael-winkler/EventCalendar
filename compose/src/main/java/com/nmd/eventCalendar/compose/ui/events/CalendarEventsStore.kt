package com.nmd.eventCalendar.compose.ui.events

import com.nmd.eventCalendar.compose.model.Event
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

/**
 * Store abstraction for calendar events.
 *
 * This interface defines a simple contract for providing calendar events to the UI in a way that is
 * efficient for Jetpack Compose.
 *
 * ## Grouped events
 * Events are exposed grouped by their [LocalDate] so the calendar UI can render each day with a
 * fast lookup (e.g., `eventsByDate[date].orEmpty()`), avoiding repeated filtering of a flat list.
 *
 * ## Reactive updates
 * [eventsByDateFlow] is a hot [StateFlow]. Consumers (Compose UI) should collect it (e.g., via
 * [androidx.lifecycle.compose.collectAsStateWithLifecycle]) to automatically recompose when the event data changes.
 *
 * ## Updating events
 * Implementations should ensure that [setEvents] updates the underlying state so that
 * [eventsByDateFlow] emits a new value when the content changes.
 *
 * Threading note: Implementations may choose how and where grouping or sorting is performed
 * (e.g., on a background dispatcher), but updates must ultimately be published through the flow.
 */
interface CalendarEventsStore {

    /**
     * A hot [StateFlow] emitting the current events grouped by date.
     *
     * The map key is the calendar day ([LocalDate]), and the value is the list of events for that day.
     * The lists are typically pre-sorted for display (e.g. by time, then name), depending on the
     * implementation.
     */
    val eventsByDateFlow: StateFlow<Map<LocalDate, List<Event>>>

    /**
     * Replaces the current set of events.
     *
     * Implementations should update their internal observable state so [eventsByDateFlow] emits a
     * new map reflecting the new events.
     *
     * @param events The new list of events to store.
     */
    fun setEvents(events: List<Event>)
}