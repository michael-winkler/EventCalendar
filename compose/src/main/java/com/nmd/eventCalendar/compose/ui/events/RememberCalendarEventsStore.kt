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
 *   navigation destination) and identified by [key]: calls with the same key share one store, so
 *   give **each calendar on a screen its own key** (e.g. `"work"`, `"holidays"`).
 *
 * The store exposes a [kotlinx.coroutines.flow.StateFlow] of events via
 * [CalendarEventsStore.eventsFlow].
 *
 * @param initialEvents Initial list of events used to seed the store (only if the store is empty).
 * @param key Identifies the store within the current owner. Use a distinct, stable value per
 * calendar; calls with the same key share their events.
 * @return A stable [CalendarEventsStore] instance.
 */
@Composable
fun rememberCalendarEventsStore(
    initialEvents: List<Event> = emptyList(),
    key: String
): CalendarEventsStore = rememberStore(initialEvents, viewModelKey = "CalendarEventsViewModel:$key")

/**
 * Returns a [CalendarEventsStore] without a key.
 *
 * All calls without a key on the same screen share ONE store, so several calendars would display
 * (and overwrite) the same events. Pass a key per calendar instead; existing calls keep working
 * unchanged until they are migrated.
 *
 * @param initialEvents Initial list of events used to seed the store (only if the store is empty).
 * @return A stable [CalendarEventsStore] instance shared by all keyless calls of the owner.
 */
@Deprecated(
    message = "Without a key, every calendar on the screen shares one store and shows the same " +
            "events. Pass a stable key per calendar, e.g. key = \"work\".",
    replaceWith = ReplaceWith("rememberCalendarEventsStore(initialEvents, key = \"calendar\")"),
    level = DeprecationLevel.WARNING
)
@Composable
fun rememberCalendarEventsStore(
    initialEvents: List<Event> = emptyList()
): CalendarEventsStore = rememberStore(initialEvents, viewModelKey = null)

@Composable
private fun rememberStore(
    initialEvents: List<Event>,
    viewModelKey: String?
): CalendarEventsStore {
    // A new store is seeded in its initializer, i.e. synchronously, so the events are there on the
    // calendar's very first frame (an effect would only run after that frame).
    val vm: CalendarEventsViewModel =
        viewModel(key = viewModelKey) {
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
