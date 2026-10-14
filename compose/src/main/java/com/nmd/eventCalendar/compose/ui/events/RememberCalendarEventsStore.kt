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
 * - Stores are scoped to the current [androidx.lifecycle.ViewModelStoreOwner] (Activity, Fragment or
 *   navigation destination) and identified by [key]. Calls without a key share one store per owner,
 *   so when showing **more than one calendar** on the same screen give each one its own [key] —
 *   otherwise they all display (and overwrite) the same events.
 *
 * The store exposes a [kotlinx.coroutines.flow.StateFlow] of events via
 * [CalendarEventsStore.eventsFlow].
 *
 * @param initialEvents Initial list of events used to seed the store (only if the store is empty).
 * @param key Optional identifier that separates this store from other calendars' stores on the same
 * screen. Use a distinct, stable value per calendar (e.g. `"work"`, `"holidays"`). `null` (default)
 * keeps the previous behaviour of one shared store per owner.
 * @return A stable [CalendarEventsStore] instance.
 */
@Composable
fun rememberCalendarEventsStore(
    initialEvents: List<Event> = emptyList(),
    key: String? = null
): CalendarEventsStore {
    // A new store is seeded in its initializer, i.e. synchronously, so the events are there on the
    // calendar's very first frame (an effect would only run after that frame).
    val vm: CalendarEventsViewModel =
        viewModel(key = key?.let { "CalendarEventsViewModel:$it" }) {
            CalendarEventsViewModel().apply { setEvents(initialEvents) }
        }

    // An existing store that is still empty picks up initialEvents passed later.
    LaunchedEffect(vm, initialEvents) {
        if (vm.rawEvents.value.isEmpty() && initialEvents.isNotEmpty()) {
            vm.setEvents(initialEvents)
        }
    }

    return remember(vm) {
        object : CalendarEventsStore {
            override val eventsFlow = vm.eventsFlow
            override fun setEvents(events: List<Event>) = vm.setEvents(events)
        }
    }
}