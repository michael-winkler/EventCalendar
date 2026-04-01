package com.nmd.eventCalendar.compose.ui.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.nmd.eventCalendar.compose.model.Event

/**
 * A composable-backed store for calendar [com.nmd.eventCalendar.compose.model.Event]s.
 *
 * The store is responsible for holding the current list of events and exposing it to Compose.
 * Implementations should ensure that:
 * - [events] returns a value that triggers recomposition when the underlying data changes
 *   (e.g. by using `mutableStateOf`, `SnapshotStateList`, `StateFlow` + `collectAsState`, etc.).
 * - [setEvents] updates the underlying state accordingly.
 *
 * Note: [events] is marked as [androidx.compose.runtime.Composable] so implementations can read Compose state directly.
 */
@Immutable
interface CalendarEventsStore {

    /**
     * Returns the current list of events.
     *
     * This function is composable so it can read Compose state and automatically recompose
     * callers when the events change.
     */
    @Composable
    fun events(): List<Event>

    /**
     * Replaces the current list of events.
     *
     * Implementations should update their internal observable state so UI recomposes.
     */
    fun setEvents(events: List<Event>)
}