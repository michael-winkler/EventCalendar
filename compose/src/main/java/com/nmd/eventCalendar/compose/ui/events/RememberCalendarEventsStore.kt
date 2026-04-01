package com.nmd.eventCalendar.compose.ui.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.viewmodel.CalendarEventsViewModel

/**
 * Public API: Returns a [CalendarEventsStore] backed by a [CalendarEventsViewModel],
 * so the event list survives configuration changes (e.g. device rotation).
 *
 * Behavior:
 * - [initialEvents] are applied only if the store is currently empty.
 * - No shuffling/sample-data generation logic is included in the library.
 *
 * @param initialEvents Initial list of events to seed the store with (only if empty).
 * @return A [CalendarEventsStore] that exposes the current events and allows updating them.
 */
@Composable
fun rememberCalendarEventsStore(
    initialEvents: List<Event> = emptyList()
): CalendarEventsStore {
    val vm: CalendarEventsViewModel = viewModel()

    LaunchedEffect(initialEvents) {
        if (vm.events.isEmpty() && initialEvents.isNotEmpty()) {
            vm.events = initialEvents
        }
    }

    return remember(vm) {
        object : CalendarEventsStore {
            @Composable
            override fun events(): List<Event> = vm.events

            override fun setEvents(events: List<Event>) {
                vm.events = events
            }
        }
    }
}