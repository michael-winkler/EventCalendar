package com.nmd.eventCalendar.compose.util

import androidx.compose.ui.graphics.Color
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.EventTimeRange
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FormattingTest {

    @Test
    fun twoDigitsPadsSingleDigitsAndKeepsLongerNumbers() {
        assertEquals("00", twoDigits(0))
        assertEquals("07", twoDigits(7))
        assertEquals("23", twoDigits(23))
        assertEquals("120", twoDigits(120))
        assertEquals("-5", twoDigits(-5)) // same as "%02d".format(-5)
    }

    @Test
    fun timeRangeFormattingIsUnchanged() {
        val range = EventTimeRange(9, 5, 10, 30)
        assertEquals("09:05", range.getFormattedStart())
        assertEquals("10:30", range.getFormattedEnd())
        assertEquals("01:25", range.getDurationFormatted())
        assertEquals("00:00", EventTimeRange(10, 0, 9, 0).getDurationFormatted())
    }

    @Test
    fun dayCellLabelNamesTheDateAndItsEvents() {
        val date = LocalDate(2026, 10, 14)
        assertEquals("14 Oktober 2026", dayCellLabel(CalendarDay(date, isCurrentMonth = true), "Oktober"))

        val events = listOf(
            Event(date, "Meeting", Color.Red, Color.White),
            Event(date, "Gym", Color.Blue, Color.White)
        )
        assertEquals(
            "14 October 2026: Meeting, Gym",
            dayCellLabel(CalendarDay(date, isCurrentMonth = true, events = events), "October")
        )
    }
}
