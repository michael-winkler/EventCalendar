package com.nmd.eventCalendar.compose.util

import androidx.compose.ui.graphics.Color
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.EventTimeRange
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
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
}
