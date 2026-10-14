package com.nmd.eventCalendar.compose.util

import androidx.compose.ui.graphics.Color
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.EventTimeRange
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.temporal.IsoFields

class UtilTest {

    private fun javaIsoWeek(date: LocalDate): Int =
        java.time.LocalDate.of(date.year, date.month.ordinal + 1, date.day)
            .get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)

    @Test
    fun isoWeekNumberMatchesJavaTimeForEveryDayOver30Years() {
        var date = LocalDate(2000, 1, 1)
        val end = LocalDate(2030, 1, 1)
        while (date < end) {
            assertEquals("ISO week of $date", javaIsoWeek(date), date.isoWeekNumber())
            date = date.plus(1, DateTimeUnit.DAY)
        }
    }

    @Test
    fun mondayRowsUseTheIsoWeekOfTheirFirstDay() {
        val monday = LocalDate(2026, 1, 5)
        assertEquals(2, isoWeekNumberOfRow(monday))
        assertEquals(monday.isoWeekNumber(), isoWeekNumberOfRow(monday))
    }

    /**
     * Regression: rows starting on Sunday showed the ISO week of that Sunday, which belongs to the
     * previous ISO week, so every row was labelled one week too low.
     */
    @Test
    fun sundayRowsUseTheIsoWeekOfTheirMondayToSaturday() {
        // Sun 4 Jan 2026 .. Sat 10 Jan 2026: Mon–Sat are ISO week 2.
        assertEquals(2, isoWeekNumberOfRow(LocalDate(2026, 1, 4)))
        // Sun 28 Dec 2025 .. Sat 3 Jan 2026: Mon–Sat are ISO week 1 of 2026.
        assertEquals(1, isoWeekNumberOfRow(LocalDate(2025, 12, 28)))
    }

    @Test
    fun everyRowIsLabelledWithTheIsoWeekOfItsThursday() {
        var rowStart = LocalDate(2020, 1, 1)
        val end = LocalDate(2030, 1, 1)
        while (rowStart < end) {
            val row = (0 until 7).map { rowStart.plus(it, DateTimeUnit.DAY) }
            val thursday = row.single { it.dayOfWeek == DayOfWeek.THURSDAY }
            assertEquals("row starting $rowStart", javaIsoWeek(thursday), isoWeekNumberOfRow(rowStart))
            rowStart = rowStart.plus(1, DateTimeUnit.DAY)
        }
    }

    /**
     * Regression: the 1- and 3-day time grid started at the week start, so a 1-day view showed
     * Monday all week and a 3-day view hid today from Thursday on.
     */
    @Test
    fun shortTimeGridViewsStartWithToday() {
        val thursday = LocalDate(2026, 10, 15)

        assertEquals(listOf(thursday), visibleTimeGridDays(thursday, DayOfWeek.MONDAY, 1))
        assertEquals(
            listOf(thursday, LocalDate(2026, 10, 16), LocalDate(2026, 10, 17)),
            visibleTimeGridDays(thursday, DayOfWeek.MONDAY, 3)
        )
    }

    @Test
    fun weekTimeGridViewShowsTheWeekAlignedToWeekStart() {
        val thursday = LocalDate(2026, 10, 15)

        val mondayWeek = visibleTimeGridDays(thursday, DayOfWeek.MONDAY, 7)
        assertEquals(LocalDate(2026, 10, 12), mondayWeek.first())
        assertEquals(LocalDate(2026, 10, 18), mondayWeek.last())

        val sundayWeek = visibleTimeGridDays(thursday, DayOfWeek.SUNDAY, 7)
        assertEquals(LocalDate(2026, 10, 11), sundayWeek.first())
        assertEquals(7, sundayWeek.size)
    }

    @Test
    fun visibleDayCountIsCoercedIntoOneToSeven() {
        val today = LocalDate(2026, 10, 15)
        assertEquals(1, visibleTimeGridDays(today, DayOfWeek.MONDAY, 0).size)
        assertEquals(7, visibleTimeGridDays(today, DayOfWeek.MONDAY, 12).size)
    }

    @Test
    fun currentWeekOnlyFollowsTheGivenToday() {
        val today = LocalDate(2026, 11, 1) // a Sunday
        val days = generateMonthDays(
            yearMonth = com.nmd.eventCalendar.compose.model.YearMonth.from(today),
            weekStart = DayOfWeek.MONDAY,
            isCurrentWeekOnly = true,
            today = today
        )
        assertEquals(LocalDate(2026, 10, 26), days.first().date)
        assertEquals(today, days.last().date)
    }

    @Test
    fun monthGridHas42DaysStartingOnTheWeekStart() {
        for (weekStart in DayOfWeek.entries) {
            for (month in 1..12) {
                val ym = com.nmd.eventCalendar.compose.model.YearMonth(2026, kotlinx.datetime.Month(month))
                val days = generateMonthDays(ym, weekStart = weekStart)
                assertEquals(42, days.size)
                assertEquals(weekStart, days.first().date.dayOfWeek)
                // consecutive and containing every day of the month, flagged as current month
                days.zipWithNext().forEach { (a, b) -> assertEquals(a.date.plus(1, DateTimeUnit.DAY), b.date) }
                val inMonth = days.filter { it.isCurrentMonth }
                assertEquals(ym.atDay(1), inMonth.first().date)
                assertEquals(inMonth.size, inMonth.map { it.date }.toSet().size)
                assertTrue(days.all { it.isCurrentMonth == (it.date.month == ym.month && it.date.year == ym.year) })
                // the first row contains the 1st of the month
                assertTrue(days.take(7).any { it.date == ym.atDay(1) })
            }
        }
    }

    @Test
    fun dayOfWeekPlusWrapsAround() {
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.SUNDAY.plus(1L))
        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.MONDAY.plus(-2L))
        assertEquals(DayOfWeek.WEDNESDAY, DayOfWeek.WEDNESDAY.plus(14L))
    }

    @Test
    fun segmentsSplitAMultiDayEventAtWeekBoundaries() {
        val event = com.nmd.eventCalendar.compose.model.Event(
            date = LocalDate(2026, 10, 9), // Fri
            name = "Reise",
            shapeColor = androidx.compose.ui.graphics.Color.Blue,
            textColor = androidx.compose.ui.graphics.Color.White,
            endDate = LocalDate(2026, 10, 14) // Wed
        )
        val week1 = (0 until 7).map { LocalDate(2026, 10, 5).plus(it, DateTimeUnit.DAY) } // Mon 5 .. Sun 11
        val week2 = (0 until 7).map { LocalDate(2026, 10, 12).plus(it, DateTimeUnit.DAY) } // Mon 12 .. Sun 18

        val s1 = segmentsForWeek(week1, listOf(event)) { 0 }.single()
        assertEquals(4 to 6, s1.startColumn to s1.endColumn)
        assertEquals(false to true, s1.continuesBefore to s1.continuesAfter)

        val s2 = segmentsForWeek(week2, listOf(event)) { 0 }.single()
        assertEquals(0 to 2, s2.startColumn to s2.endColumn)
        assertEquals(true to false, s2.continuesBefore to s2.continuesAfter)

        val week0 = (0 until 7).map { LocalDate(2026, 9, 28).plus(it, DateTimeUnit.DAY) }
        assertTrue(segmentsForWeek(week0, listOf(event)) { 0 }.isEmpty())
    }

    @Test
    fun overlappingEventsGetDifferentLanesAndLanesAreReused() {
        fun e(name: String, from: Int, to: Int) = com.nmd.eventCalendar.compose.model.Event(
            date = LocalDate(2026, 10, from),
            name = name,
            shapeColor = androidx.compose.ui.graphics.Color.Blue,
            textColor = androidx.compose.ui.graphics.Color.White,
            endDate = LocalDate(2026, 10, to)
        )
        val a = e("A", 1, 5)
        val b = e("B", 3, 4)
        val c = e("C", 6, 7) // starts after A and B ended -> may reuse lane 0

        val lanes = assignEventLanes(listOf(c, b, a))

        assertNotEquals(lanes.getValue(a), lanes.getValue(b))
        assertEquals(0, lanes.getValue(a)) // longest first on the same start date order
        assertEquals(0, lanes.getValue(c))
    }

    @Test
    fun timeGridScrollsToTheEarliestTimedEventOrToTheDefaultHour() {
        val day = LocalDate(2026, 10, 14)
        fun timed(start: Int, end: Int) =
            Event(day, "T$start", Color.Red, Color.White, timeRange = EventTimeRange(start, 0, end, 0))
        val allDay = Event(day, "All day", Color.Red, Color.White)
        val invalid = Event(day, "Invalid", Color.Red, Color.White, timeRange = EventTimeRange(10, 0, 9, 0))

        assertEquals(DefaultTimeGridStartHour, initialTimeGridHour(emptyList()))
        assertEquals(DefaultTimeGridStartHour, initialTimeGridHour(listOf(allDay, invalid)))
        assertEquals(6, initialTimeGridHour(listOf(timed(14, 15), allDay, timed(6, 7))))
    }

    @Test
    fun eventsWithoutAValidTimeRangeBelongToTheAllDayArea() {
        val day = LocalDate(2026, 10, 14)
        assertTrue(isAllDayInTimeGrid(Event(day, "A", Color.Red, Color.White)))
        assertTrue(isAllDayInTimeGrid(Event(day, "B", Color.Red, Color.White, timeRange = EventTimeRange(10, 0, 9, 0))))
        assertFalse(isAllDayInTimeGrid(Event(day, "C", Color.Red, Color.White, timeRange = EventTimeRange(9, 0, 10, 0))))
    }
}
