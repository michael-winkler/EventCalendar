package com.nmd.eventCalendar.compose.ui.events

import com.nmd.eventCalendar.compose.model.Event
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

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

    /**
     * Events grouped by their **start** date.
     *
     * @deprecated Replaced by the flat [eventsFlow]. This alias groups events by their start date
     * only (it does not expand multi-day events across the days they cover); to get every event on a
     * given day, use `eventsFlow.value.filter { it.occursOn(date) }`. Kept as a non-breaking bridge
     * and derived on demand from [eventsFlow]; it will be removed in a future release.
     */
    @Deprecated(
        message = "Use eventsFlow and resolve a day with Event.occursOn(date). " +
            "eventsByDateFlow groups by start date only and will be removed in a future release.",
        replaceWith = ReplaceWith("eventsFlow"),
        level = DeprecationLevel.WARNING
    )
    val eventsByDateFlow: StateFlow<Map<LocalDate, List<Event>>>
        get() = GroupedByStartDateStateFlow(eventsFlow)
}

/**
 * Read-only [StateFlow] adapter that presents [source] (a flat event list) grouped by start date.
 *
 * Backs the deprecated [CalendarEventsStore.eventsByDateFlow]; the grouping is computed lazily from
 * the current list, so it costs O(number of events) and only when actually collected/read.
 */
private class GroupedByStartDateStateFlow(
    private val source: StateFlow<List<Event>>
) : StateFlow<Map<LocalDate, List<Event>>> {

    override val value: Map<LocalDate, List<Event>>
        get() = source.value.groupBy { it.date }

    override val replayCache: List<Map<LocalDate, List<Event>>>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<Map<LocalDate, List<Event>>>): Nothing {
        source.collect { events -> collector.emit(events.groupBy { it.date }) }
    }
}
