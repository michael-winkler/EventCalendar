package com.nmd.eventCalendar.compose.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nmd.eventCalendar.compose.model.Event

/**
 * Holds the current list of calendar [com.nmd.eventCalendar.compose.model.Event]s.
 *
 * The list is stored as Compose state ([androidx.compose.runtime.mutableStateOf]) so Compose UI observing it will
 * automatically recompose when the events are replaced.
 *
 * Note: Updates should be done by assigning a new list instance to [events]
 * (e.g. `events = events + newEvent`), not by mutating an existing list in place.
 */
class CalendarEventsViewModel : ViewModel() {
    var events: List<Event> by mutableStateOf(emptyList())
}