package com.nmd.eventCalendar.compose.util

import com.nmd.eventCalendar.compose.model.Event
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

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
