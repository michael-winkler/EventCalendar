package com.nmd.eventCalendar.compose.ui.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nmd.eventCalendar.compose.model.Event

/**
 * Simple in-memory [CalendarEventsStore] intended for previews and tests.
 *
 * It keeps events in Compose state so UI recomposes when [setEvents] replaces the list.
 *
 * @param initialEvents Initial events shown by the preview store.
 */
@Immutable
internal class PreviewCalendarEventsStore(
    initialEvents: List<Event>
) : CalendarEventsStore {

    private var _events by mutableStateOf(initialEvents)

    /** Returns the current events (observable Compose state). */
    @Composable
    override fun events(): List<Event> = _events

    /** Replaces the current events list and triggers recomposition. */
    override fun setEvents(events: List<Event>) {
        _events = events
    }
}