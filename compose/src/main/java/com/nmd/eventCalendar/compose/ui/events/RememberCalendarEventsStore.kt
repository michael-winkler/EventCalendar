package com.nmd.eventCalendar.compose.ui.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.viewmodel.CalendarEventsViewModel

/**
 * Public API that returns a [CalendarEventsStore] backed by a [CalendarEventsViewModel],
 * so the event data survives configuration changes (e.g., device rotation).
 *
 * Behavior:
 * - [initialEvents] are applied only if the current store is empty.
 *
 * The store exposes a [kotlinx.coroutines.flow.StateFlow] of events grouped by date via
 * [CalendarEventsStore.eventsByDateFlow].
 *
 * @param initialEvents Initial list of events used to seed the store (only if the store is empty).
 * @return A stable [CalendarEventsStore] instance.
 */
@Composable
fun rememberCalendarEventsStore(
    initialEvents: List<Event> = emptyList()
): CalendarEventsStore {
    val vm: CalendarEventsViewModel = viewModel()

    LaunchedEffect(initialEvents) {
        if (vm.rawEvents.value.isEmpty() && initialEvents.isNotEmpty()) {
            vm.setEvents(initialEvents)
        }
    }

    return remember(vm) {
        object : CalendarEventsStore {
            override val eventsByDateFlow = vm.eventsByDateFlow
            override fun setEvents(events: List<Event>) = vm.setEvents(events)
        }
    }
}