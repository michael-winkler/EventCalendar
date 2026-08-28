package com.nmd.eventCalendar.compose.util

import androidx.compose.ui.graphics.Color
import com.nmd.eventCalendar.compose.model.Event
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

/**
 * A resolved placement of an [event] inside a single calendar week.
 *
 * A multi-day event that crosses a week boundary produces one [EventSegment] per week it touches.
 * Columns are 0-based indices into the week (0 = first visible day, 6 = last visible day) and are
 * therefore already expressed relative to the configured week start.
 *
 * @property event The event this segment belongs to.
 * @property lane The vertical lane (row) the event occupies. Lanes are assigned per month grid so a
 * multi-day event keeps the same lane across consecutive weeks, producing a visually continuous bar.
 * @property startColumn First covered column within the week (inclusive, 0..6).
 * @property endColumn Last covered column within the week (inclusive, 0..6).
 * @property continuesBefore True if the event started in an earlier week (bar is flat on the left).
 * @property continuesAfter True if the event continues into a later week (bar is flat on the right).
 */
internal data class EventSegment(
    val event: Event,
    val lane: Int,
    val startColumn: Int,
    val endColumn: Int,
    val continuesBefore: Boolean,
    val continuesAfter: Boolean
)

/**
 * Returns the events in a stable display order: by start date, then start time, then name.
 *
 * Used as the canonical ordering held by the events store so that per-day filtering downstream
 * preserves a predictable order without re-sorting.
 */
internal fun List<Event>.sortedForDisplay(): List<Event> =
    sortedWith(
        compareBy<Event> { it.date }
            .thenBy { it.timeRange?.startHour ?: Int.MAX_VALUE }
            .thenBy { it.timeRange?.startMinute ?: Int.MAX_VALUE }
            .thenBy { it.name }
    )

/**
 * Identity used to decide whether two separate events are "the same type" for auto-merging.
 */
private data class EventTypeKey(
    val name: String,
    val shapeColor: Color,
    val textColor: Color,
    val autoAdjustTextColorForBackground: Boolean
)

private fun Event.typeKey() =
    EventTypeKey(name, shapeColor, textColor, autoAdjustTextColorForBackground)

/**
 * Combines separate single-day events of the same type on consecutive days into a single continuous
 * multi-day event (with an [Event.endDate] spanning the run).
 *
 * This is the same transformation the month view applies when
 * [com.nmd.eventCalendar.compose.ui.config.CalendarOptions.mergeAdjacentEvents] is enabled. It lets
 * callers who model a multi-day event as several independent [Event] objects (one per day) still get
 * a single continuous bar without changing their data — and it is exposed publicly so the same merged
 * view can be reused elsewhere (e.g. to show the full span of a tapped event in a details sheet).
 *
 * To avoid wrongly merging genuinely independent occurrences (e.g. a daily recurring appointment),
 * merging is deliberately conservative:
 *
 * - Only **all-day** events are considered (`timeRange == null`); timed events are never merged, as a
 *   repeating time-of-day almost always signals a recurring appointment rather than a span.
 * - Only pure single-day events participate (`endDate == null`); events that already declare an
 *   explicit end are passed through untouched.
 * - Events must share the exact same type (name + colors) and cover a strictly consecutive, gap-free
 *   chain of days.
 * - A day may legitimately hold more than one event of the same type: exactly one representative per
 *   day forms the span, and any additional same-type events on those days are kept as their own
 *   single-day chips (they are neither dropped nor allowed to break the span).
 *
 * The synthesized span reuses the representative event's identity (via [Event.copy]) so stable keys
 * stay stable.
 *
 * @param events The events to merge.
 * @return A list where eligible consecutive same-type events are replaced by one spanning event; all
 * other events are returned unchanged.
 */
fun mergeAdjacentEvents(events: List<Event>): List<Event> {
    if (events.size < 2) return events

    val result = ArrayList<Event>(events.size)
    val mergeable = ArrayList<Event>()

    for (event in events) {
        if (event.timeRange == null && event.endDate == null) {
            mergeable.add(event)
        } else {
            // Timed events or events that already declare an explicit span are never auto-merged.
            result.add(event)
        }
    }

    if (mergeable.isEmpty()) return events

    for ((_, group) in mergeable.groupBy { it.typeKey() }) {
        val byDate = group.groupBy { it.date }
        val dates = byDate.keys.sorted()

        var i = 0
        while (i < dates.size) {
            var j = i
            while (j + 1 < dates.size &&
                dates[j].plus(1, DateTimeUnit.DAY) == dates[j + 1]
            ) {
                j++
            }

            // One representative per day forms the run; a run of >= 2 days becomes a single span.
            val representative = byDate.getValue(dates[i]).first()
            result.add(
                if (j > i) representative.copy(endDate = dates[j]) else representative
            )

            // Any additional same-type events on these days are kept as separate single-day chips
            // instead of dropping them or letting them cancel the merge.
            for (k in i..j) {
                val onDay = byDate.getValue(dates[k])
                for (extraIndex in 1 until onDay.size) {
                    result.add(onDay[extraIndex])
                }
            }

            i = j + 1
        }
    }

    return result
}

/**
 * Assigns a stable lane index to each event so that no two overlapping events share a lane.
 *
 * Events are placed greedily in date order, reusing the lowest lane whose previously placed event
 * has already ended. Because the assignment spans the whole set of events (not a single week), a
 * multi-day event keeps one lane across every week it covers, which lets the UI render it as a
 * single continuous bar.
 *
 * @param events The distinct events visible in the current grid.
 * @return A map of [Event.id] to lane index (0-based).
 */
internal fun assignEventLanes(events: List<Event>): Map<Int, Int> {
    if (events.isEmpty()) return emptyMap()

    val sorted = events.sortedWith(
        compareBy<Event> { it.date }
            .thenByDescending { it.spanDays }
            .thenBy { it.name }
            .thenBy { it.id }
    )

    val laneLastDate = ArrayList<LocalDate>()
    val laneByEventId = HashMap<Int, Int>(events.size)

    for (event in sorted) {
        var lane = 0
        while (lane < laneLastDate.size && event.date <= laneLastDate[lane]) {
            lane++
        }
        if (lane == laneLastDate.size) {
            laneLastDate.add(event.lastDate)
        } else {
            laneLastDate[lane] = event.lastDate
        }
        laneByEventId[event.id] = lane
    }

    return laneByEventId
}

/**
 * Computes the [EventSegment]s for a single week.
 *
 * @param weekDates The 7 dates of the week, ordered by the configured week start.
 * @param events The distinct events visible in the current grid.
 * @param laneOf Resolver returning the lane assigned to an event (see [assignEventLanes]).
 * @return Segments overlapping this week, sorted by lane then start column.
 */
internal fun segmentsForWeek(
    weekDates: List<LocalDate>,
    events: List<Event>,
    laneOf: (Event) -> Int
): List<EventSegment> {
    if (weekDates.isEmpty()) return emptyList()

    val weekStart = weekDates.first()
    val weekEnd = weekDates.last()
    val lastColumn = weekDates.lastIndex

    return events
        .mapNotNull { event ->
            if (event.lastDate < weekStart || event.date > weekEnd) return@mapNotNull null

            val startColumn =
                if (event.date <= weekStart) 0 else weekStart.daysUntil(event.date)
            val endColumn =
                if (event.lastDate >= weekEnd) lastColumn else weekStart.daysUntil(event.lastDate)

            EventSegment(
                event = event,
                lane = laneOf(event),
                startColumn = startColumn.coerceIn(0, lastColumn),
                endColumn = endColumn.coerceIn(0, lastColumn),
                continuesBefore = event.date < weekStart,
                continuesAfter = event.lastDate > weekEnd
            )
        }
        .sortedWith(compareBy({ it.lane }, { it.startColumn }))
}
