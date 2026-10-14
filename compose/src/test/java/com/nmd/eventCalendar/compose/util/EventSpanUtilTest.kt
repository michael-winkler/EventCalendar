package com.nmd.eventCalendar.compose.util

import androidx.compose.ui.graphics.Color
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.EventTimeRange
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Adversarial test data for the auto-merge feature ([mergeAdjacentEvents]).
 *
 * Each test encodes a scenario that previously produced a broken month-view rendering and pins down
 * the expected, fixed behaviour.
 */
class EventSpanUtilTest {

    private val blue = Color(0xFF4BADEB)
    private val orange = Color(0xFFE07912)
    private val white = Color.White

    private fun day(d: Int) = LocalDate(2026, 8, d)

    private fun allDay(name: String, d: Int, color: Color = blue) =
        Event(date = day(d), name = name, shapeColor = color, textColor = white)

    private fun spansOf(events: List<Event>) =
        events.filter { it.isMultiDay }.sortedBy { it.date }

    @Test
    fun mergesConsecutiveAllDaySameTypeIntoOneSpan() {
        val input = listOf(allDay("Urlaub", 3), allDay("Urlaub", 4), allDay("Urlaub", 5))

        val result = mergeAdjacentEvents(input)

        assertEquals(1, result.size)
        val span = result.single()
        assertEquals(day(3), span.date)
        assertEquals(day(5), span.endDate)
        assertEquals(3, span.spanDays)
    }

    @Test
    fun doesNotMergeAcrossAGap() {
        // Mon, Tue, then a gap, then Thu -> a 2-day span and a separate single day.
        val input = listOf(allDay("Urlaub", 3), allDay("Urlaub", 4), allDay("Urlaub", 6))

        val result = mergeAdjacentEvents(input)

        assertEquals(2, result.size)
        val span = spansOf(result).single()
        assertEquals(day(3), span.date)
        assertEquals(day(4), span.endDate)
        assertTrue(result.any { !it.isMultiDay && it.date == day(6) })
    }

    @Test
    fun neverMergesTimedEvents() {
        // A daily recurring timed appointment must stay as separate occurrences.
        val input = (3..5).map {
            Event(
                date = day(it),
                name = "Gym",
                shapeColor = orange,
                textColor = white,
                timeRange = EventTimeRange(18, 0, 19, 0)
            )
        }

        val result = mergeAdjacentEvents(input)

        assertEquals(3, result.size)
        assertTrue(result.none { it.isMultiDay })
    }

    @Test
    fun doesNotMergeDifferentColorsOfSameName() {
        val input = listOf(allDay("Shift", 3, blue), allDay("Shift", 4, orange))

        val result = mergeAdjacentEvents(input)

        assertEquals(2, result.size)
        assertTrue(result.none { it.isMultiDay })
    }

    @Test
    fun leavesExplicitMultiDayEventsUntouched() {
        val explicit = Event(
            date = day(3),
            name = "Trip",
            shapeColor = blue,
            textColor = white,
            endDate = day(7)
        )

        val result = mergeAdjacentEvents(listOf(explicit, allDay("Other", 10, orange)))

        assertTrue(result.contains(explicit))
    }

    /**
     * Regression: a duplicate same-type event on a single day of a chain used to cancel the ENTIRE
     * merge for that type, breaking the intended span into isolated chips. The span must survive and
     * the extra event must be kept as its own single-day chip.
     */
    @Test
    fun duplicateOnOneChainDayKeepsSpanAndExtraChip() {
        val input = listOf(
            allDay("Workshop", 3),
            allDay("Workshop", 4),
            allDay("Workshop", 5),
            allDay("Workshop", 5) // duplicate on the last day
        )

        val result = mergeAdjacentEvents(input)

        // One 3-day span ...
        val span = spansOf(result).single()
        assertEquals(day(3), span.date)
        assertEquals(day(5), span.endDate)
        // ... plus exactly one leftover single-day chip on day 5.
        val singles = result.filter { !it.isMultiDay }
        assertEquals(1, singles.size)
        assertEquals(day(5), singles.single().date)
        assertNull(singles.single().endDate)
    }

    @Test
    fun mergesTwoSeparateRunsOfTheSameType() {
        val input = listOf(
            allDay("Urlaub", 3), allDay("Urlaub", 4),      // run 1
            allDay("Urlaub", 10), allDay("Urlaub", 11)     // run 2 (after a gap)
        )

        val spans = spansOf(mergeAdjacentEvents(input))

        assertEquals(2, spans.size)
        assertEquals(day(3) to day(4), spans[0].date to spans[0].endDate)
        assertEquals(day(10) to day(11), spans[1].date to spans[1].endDate)
    }

    /**
     * Regression: lanes used to be keyed by [Event.id]. Events that share an id (e.g. built with
     * `copy(date = ...)`, which keeps the id) overwrote each other's lane, so overlapping bars were
     * drawn on top of each other.
     */
    @Test
    fun overlappingEventsWithSharedIdGetSeparateLanes() {
        val base = Event(
            date = day(3),
            name = "Kurs",
            shapeColor = blue,
            textColor = white,
            endDate = day(5)
        )
        val sameIdOverlapping = base.copy(name = "Kurs B", date = day(4), endDate = day(6))
        assertEquals(base.id, sameIdOverlapping.id)

        val lanes = assignEventLanes(listOf(base, sameIdOverlapping))

        assertEquals(2, lanes.size)
        assertNotEquals(lanes.getValue(base), lanes.getValue(sameIdOverlapping))
    }

    /**
     * Regression: the month view de-duplicated its events by [Event.id], so a recurring event built
     * via `copy(date = ...)` showed up on its first day only.
     */
    @Test
    fun copiesWithSharedIdAreAllRenderedInTheirWeek() {
        val base = allDay("Training", 3, orange)
        val recurring = listOf(base, base.copy(date = day(5)), base.copy(date = day(7)))
        val week = (3..9).map { day(it) }

        val lanes = assignEventLanes(recurring)
        val segments = segmentsForWeek(week, recurring) { lanes.getValue(it) }

        assertEquals(3, segments.size)
        assertEquals(listOf(0, 2, 4), segments.map { it.startColumn }.sorted())
    }

    @Test
    fun eventsInRangeDistributesEventsOntoTheDaysTheyCover() {
        val before = Event(day(1), "Before", blue, white)                    // ends before the range
        val overlapStart = Event(day(2), "Overlap", blue, white, endDate = day(4))
        val inside = allDay("Inside", 6)
        val overlapEnd = Event(day(8), "Long", blue, white, endDate = day(20))
        val after = allDay("After", 12)                                      // starts after the range

        // Range: Aug 3..9 (7 days).
        val result = eventsInRange(listOf(before, overlapStart, inside, overlapEnd, after), day(3), 7)

        assertEquals(listOf(overlapStart, inside, overlapEnd), result.inRange)
        assertEquals(7, result.byDay.size)
        assertEquals(listOf(overlapStart), result.byDay[0])                  // Aug 3
        assertEquals(listOf(overlapStart), result.byDay[1])                  // Aug 4
        assertEquals(emptyList<Event>(), result.byDay[2])                    // Aug 5
        assertEquals(listOf(inside), result.byDay[3])                        // Aug 6
        assertEquals(listOf(overlapEnd), result.byDay[5])                    // Aug 8
        assertEquals(listOf(overlapEnd), result.byDay[6])                    // Aug 9
    }

    @Test
    fun eventsInRangeMatchesFilteringEachDay() {
        val events = (1..28).flatMap { d ->
            listOf(allDay("A$d", d), Event(day(d), "M$d", blue, white, endDate = day(minOf(31, d + d % 4))))
        }
        val result = eventsInRange(events, day(5), 14)
        (0 until 14).forEach { i ->
            val date = day(5 + i)
            assertEquals(events.filter { it.occursOn(date) }, result.byDay[i])
        }
    }

    /** Two equal events are still two bars: they must not share (and overlap on) one lane. */
    @Test
    fun equalEventsGetTheirOwnLanes() {
        val a = allDay("Same", 3)
        val b = a.copy()
        assertEquals(a, b)

        val lanes = assignEventLanes(listOf(a, b))

        assertEquals(2, lanes.size)
        assertNotEquals(lanes.getValue(a), lanes.getValue(b))
    }
}
