package com.nmd.eventCalendar.compose.ui.events

import com.nmd.eventCalendar.compose.model.Event
import kotlinx.coroutines.flow.StateFlow

/**
 * Store abstraction for calendar events.
 *
 * This interface defines a simple contract for providing calendar events to the UI in a way that is
 * efficient for Jetpack Compose.
 *
 * ## Flat event list
 * Events are exposed as a single, stably ordered [List]. The UI resolves which events fall on a
 * given day on demand (via [Event.occursOn]); this keeps multi-day events cheap regardless of how
 * long they span, because the cost scales with the number of events, not with the total number of
 * covered days.
 *
 * ## Reactive updates
 * [eventsFlow] is a hot [StateFlow]. Consumers (Compose UI) should collect it (e.g., via
 * [androidx.lifecycle.compose.collectAsStateWithLifecycle]) to automatically recompose when the event data changes.
 *
 * ## Updating events
 * Implementations should ensure that [setEvents] updates the underlying state so that
 * [eventsFlow] emits a new value when the content changes.
 */
interface CalendarEventsStore {

    /**
     * A hot [StateFlow] emitting the current events in a stable display order
     * (by start date, then start time, then name).
     */
    val eventsFlow: StateFlow<List<Event>>

    /**
     * Replaces the current set of events.
     *
     * Implementations should update their internal observable state so [eventsFlow] emits a new
     * list reflecting the new events.
     *
     * @param events The new list of events to store.
     */
    fun setEvents(events: List<Event>)
}
