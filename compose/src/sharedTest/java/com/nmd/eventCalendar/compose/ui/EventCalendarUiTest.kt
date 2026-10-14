package com.nmd.eventCalendar.compose.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nmd.eventCalendar.compose.EventCalendarCompose
import com.nmd.eventCalendar.compose.EventCalendarWeekTime
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.EventTimeRange
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventCalendar.compose.ui.components.WeekNumberTestTag
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.events.CalendarEventsStore
import com.nmd.eventCalendar.compose.ui.events.PreviewCalendarEventsStore
import com.nmd.eventCalendar.compose.ui.events.rememberCalendarEventsStore
import com.nmd.eventCalendar.compose.util.toStringRes
import com.nmd.eventcalendar.compose.R
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.temporal.IsoFields
import kotlin.time.Clock

/**
 * UI tests for the public calendar composables. Shared between unit tests (JVM, Robolectric;
 * configured in `robolectric.properties`) and instrumented tests on a device/emulator.
 *
 * Each test pins down user-visible behaviour, including the regressions fixed alongside them.
 */
@RunWith(AndroidJUnit4::class)
class EventCalendarUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val thisMonth: YearMonth = YearMonth.from(today)

    private fun store(vararg events: Event): CalendarEventsStore =
        PreviewCalendarEventsStore(events.toList())

    private fun monthTitle(month: YearMonth): String {
        val name = composeRule.activity.getString(month.month.toStringRes())
        return if (month.year != today.year) "$name ${month.year}" else name
    }

    @Test
    fun headerShowsTheCurrentMonthAndNavigatesForwardAndBack() {
        val reported = mutableListOf<YearMonth>()
        composeRule.setContent {
            EventCalendarCompose(
                calendarEventsStore = store(),
                onDaySelected = {},
                onMonthChange = { reported += it }
            )
        }

        composeRule.onNodeWithText(monthTitle(thisMonth)).assertExists()

        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.month_next))
            .performClick()
        composeRule.waitForIdle()
        val next = thisMonth.plusMonths(1)
        composeRule.onNodeWithText(monthTitle(next)).assertExists()
        assertEquals(next, reported.last())

        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.month_previous))
            .performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(monthTitle(thisMonth)).assertExists()
        assertEquals(thisMonth, reported.last())
    }

    /**
     * Regression: the month view de-duplicated events by id, so recurring events created with
     * `copy(date = ...)` (which keeps the id) were shown on their first day only.
     */
    @Test
    fun recurringEventsCreatedWithCopyAreAllShown() {
        val base = Event(
            date = thisMonth.atDay(3),
            name = "Training",
            shapeColor = Color(0xFF3949AB),
            textColor = Color.White
        )
        val events = arrayOf(base, base.copy(date = thisMonth.atDay(12)), base.copy(date = thisMonth.atDay(21)))

        composeRule.setContent {
            EventCalendarCompose(
                calendarEventsStore = store(*events),
                onDaySelected = {},
                onMonthChange = {}
            )
        }

        composeRule.onAllNodesWithText("Training").assertCountEquals(3)
    }

    @Test
    fun aMultiDayEventIsDrawnOncePerWeekItTouches() {
        // 10 consecutive days always touch two or three week rows; one bar segment per row.
        val start = thisMonth.atDay(5)
        val end = start.plus(9, DateTimeUnit.DAY)
        val event = Event(
            date = start,
            name = "Urlaub",
            shapeColor = Color(0xFF039BE5),
            textColor = Color.White,
            endDate = end
        )
        val rowsTouched = (0..9).map { start.plus(it, DateTimeUnit.DAY) }
            .map { date -> rowStartOf(date, DayOfWeek.MONDAY) }
            .distinct().size

        composeRule.setContent {
            EventCalendarCompose(
                calendarEventsStore = store(event),
                onDaySelected = {},
                onMonthChange = {}
            )
        }

        composeRule.onAllNodesWithText("Urlaub").assertCountEquals(rowsTouched)
    }

    /**
     * Regression: with a Sunday week start every row was labelled with the ISO week of its Sunday,
     * i.e. one week too low. The label must be the ISO week of the row's Thursday.
     */
    @Test
    fun weekNumbersMatchTheIsoWeekOfEachRowWithSundayStart() {
        val expected = monthRows(thisMonth, DayOfWeek.SUNDAY).map { rowStart ->
            javaIsoWeek(rowStart.plus(4, DateTimeUnit.DAY)).toString() // Sun + 4 = Thu
        }
        composeRule.setContent {
            EventCalendarCompose(
                calendarOptions = defaultCalendarOptions().copy(
                    weekStart = DayOfWeek.SUNDAY,
                    calendarWeekVisible = true
                ),
                calendarEventsStore = store(),
                onDaySelected = {},
                onMonthChange = {}
            )
        }

        val shown = composeRule.onAllNodesWithTag(WeekNumberTestTag).fetchSemanticsNodes()
            .map { node -> node.config[SemanticsProperties.Text].joinToString("") { it.text } }
        assertEquals(expected, shown)
    }

    @Test
    fun tappingADaySelectsThatDate() {
        val selected = mutableListOf<CalendarDay>()
        composeRule.setContent {
            EventCalendarCompose(
                calendarEventsStore = store(),
                onDaySelected = { selected += it },
                onMonthChange = {}
            )
        }

        // Clickable nodes: previous button, next button, then the 42 day cells in grid order.
        val clickables = composeRule.onAllNodes(hasClickAction())
        clickables.assertCountEquals(2 + 42)
        val gridStart = monthRows(thisMonth, DayOfWeek.MONDAY).first()

        clickables[2].performClick()
        clickables[2 + 15].performClick()

        assertEquals(
            listOf(gridStart, gridStart.plus(15, DateTimeUnit.DAY)),
            selected.map { it.date }
        )
        assertEquals(gridStart.plus(15, DateTimeUnit.DAY).month == thisMonth.month, selected[1].isCurrentMonth)
    }

    /**
     * Regression: the clickable day cells had no label, so screen readers announced 42 unnamed
     * buttons. Every cell must name its date (and the events on it).
     */
    @Test
    fun dayCellsAreLabelledForScreenReaders() {
        val meeting = Event(today, "Meeting", Color.Red, Color.White)
        composeRule.setContent {
            EventCalendarCompose(
                calendarEventsStore = store(meeting),
                onDaySelected = {},
                onMonthChange = {}
            )
        }

        val gridStart = monthRows(thisMonth, DayOfWeek.MONDAY).first()
        val cells = composeRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().drop(2) // prev/next
        val labels = cells.map { it.config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString() }
        assertEquals(42, labels.size)
        val firstMonth = composeRule.activity.getString(gridStart.month.toStringRes())
        assertEquals("${gridStart.day} $firstMonth ${gridStart.year}", labels.first())

        val todayIndex = (0 until 42).first { gridStart.plus(it, DateTimeUnit.DAY) == today }
        val todayMonth = composeRule.activity.getString(today.month.toStringRes())
        assertEquals("${today.day} $todayMonth ${today.year}: Meeting", labels[todayIndex])
    }

    /**
     * Regression: the 1- and 3-day time grid started at the week start instead of today.
     */
    @Test
    fun oneDayTimeGridShowsToday() {
        composeRule.setContent {
            EventCalendarWeekTime(
                calendarOptions = defaultCalendarOptions().copy(noOfVisibleDays = 1),
                calendarEventsStore = store()
            )
        }

        composeRule.onNodeWithText(dayLabel(today)).assertExists()
        composeRule.onNodeWithText(dayLabel(today.plus(1, DateTimeUnit.DAY))).assertDoesNotExist()
        composeRule.onNodeWithText(dayLabel(today.minus(1, DateTimeUnit.DAY))).assertDoesNotExist()
    }

    @Test
    fun threeDayTimeGridShowsTodayAndTheNextTwoDays() {
        composeRule.setContent {
            EventCalendarWeekTime(
                calendarOptions = defaultCalendarOptions().copy(noOfVisibleDays = 3),
                calendarEventsStore = store()
            )
        }

        (0..2).forEach { offset ->
            composeRule.onNodeWithText(dayLabel(today.plus(offset, DateTimeUnit.DAY))).assertExists()
        }
        composeRule.onNodeWithText(dayLabel(today.plus(3, DateTimeUnit.DAY))).assertDoesNotExist()
        composeRule.onNodeWithText(dayLabel(today.minus(1, DateTimeUnit.DAY))).assertDoesNotExist()
    }

    @Test
    fun timedEventsAreShownInTheTimeGrid() {
        val meeting = Event(
            date = today,
            name = "Meeting",
            shapeColor = Color.Blue,
            textColor = Color.White,
            timeRange = EventTimeRange(9, 0, 10, 30)
        )
        composeRule.setContent {
            EventCalendarWeekTime(
                calendarOptions = defaultCalendarOptions().copy(noOfVisibleDays = 1),
                calendarEventsStore = store(meeting)
            )
        }

        composeRule.onNodeWithText("Meeting").assertExists()
        composeRule.onNodeWithText("09:00 - 10:30").assertExists()
    }

    /**
     * Events without a time range used to be missing from the time grid entirely; they are shown in
     * the all-day row, a multi-day event on every visible day it covers.
     */
    @Test
    fun allDayAndMultiDayEventsAreShownInTheTimeGrid() {
        val holiday = Event(today, "Holiday", Color.Green, Color.Black)
        val trip = Event(
            date = today.minus(1, DateTimeUnit.DAY),
            name = "Trip",
            shapeColor = Color.Blue,
            textColor = Color.White,
            endDate = today.plus(1, DateTimeUnit.DAY)
        )
        val meeting = Event(today, "Meeting", Color.Red, Color.White, timeRange = EventTimeRange(9, 0, 10, 0))
        val clicked = mutableListOf<String>()
        composeRule.setContent {
            EventCalendarWeekTime(
                calendarOptions = defaultCalendarOptions().copy(noOfVisibleDays = 3),
                calendarEventsStore = store(holiday, trip, meeting),
                onEventSelected = { clicked += it.name }
            )
        }

        composeRule.onNodeWithText(composeRule.activity.getString(R.string.all_day_label)).assertExists()
        composeRule.onAllNodesWithText("Holiday").assertCountEquals(1)
        composeRule.onAllNodesWithText("Trip").assertCountEquals(2) // today and tomorrow are visible
        composeRule.onNodeWithText("Meeting").assertExists() // timed: hour grid, not the all-day row

        composeRule.onNodeWithText("Holiday").performClick()
        assertEquals(listOf("Holiday"), clicked)
    }

    /**
     * Regression: all `rememberCalendarEventsStore()` calls on one screen shared a single
     * ViewModel, so two calendars displayed (and overwrote) the same events. Distinct keys must
     * keep them apart, equal keys share, and the deprecated keyless calls keep sharing
     * (backwards compatible).
     */
    @Suppress("DEPRECATION") // The keyless overload is exercised on purpose.
    @Test
    fun storesWithDifferentKeysAreIndependent() {
        val work = Event(today, "Work", Color.Red, Color.White)
        val holiday = Event(today, "Holiday", Color.Green, Color.Black)
        lateinit var workStore: CalendarEventsStore
        lateinit var holidayStore: CalendarEventsStore
        lateinit var sharedA: CalendarEventsStore
        lateinit var sharedB: CalendarEventsStore
        lateinit var sameKeyA: CalendarEventsStore
        lateinit var sameKeyB: CalendarEventsStore

        composeRule.setContent {
            workStore = rememberCalendarEventsStore(listOf(work), key = "work")
            holidayStore = rememberCalendarEventsStore(listOf(holiday), key = "holidays")
            sharedA = rememberCalendarEventsStore()
            sharedB = rememberCalendarEventsStore()
            sameKeyA = rememberCalendarEventsStore(key = "team")
            sameKeyB = rememberCalendarEventsStore(key = "team")
        }
        composeRule.waitForIdle()

        assertEquals(listOf("Work"), workStore.eventsFlow.value.map { it.name })
        assertEquals(listOf("Holiday"), holidayStore.eventsFlow.value.map { it.name })

        workStore.setEvents(emptyList())
        composeRule.waitForIdle()
        assertEquals(listOf("Holiday"), holidayStore.eventsFlow.value.map { it.name })

        sharedA.setEvents(listOf(work))
        composeRule.waitForIdle()
        assertEquals(listOf("Work"), sharedB.eventsFlow.value.map { it.name })

        sameKeyA.setEvents(listOf(holiday))
        assertEquals(listOf("Holiday"), sameKeyB.eventsFlow.value.map { it.name })
        assertEquals(listOf("Work"), sharedB.eventsFlow.value.map { it.name })
    }

    /**
     * Regression: eventsFlow was derived asynchronously and only while collected, so `value` was
     * stale right after setEvents (or when nobody collected), and the seeded events only appeared
     * one frame after the calendar was first shown.
     */
    @Test
    fun storeExposesEventsImmediatelyAndOnTheFirstFrame() {
        val seeded = Event(today, "Seeded", Color.Red, Color.White)
        lateinit var store: CalendarEventsStore
        val firstFrame = mutableListOf<List<String>>()

        composeRule.setContent {
            store = rememberCalendarEventsStore(listOf(seeded), key = "first-frame")
            val events by store.eventsFlow.collectAsState()
            if (firstFrame.isEmpty()) firstFrame += events.map { it.name }
        }
        composeRule.waitForIdle()
        assertEquals(listOf(listOf("Seeded")), firstFrame)

        val later = Event(today, "B", Color.Blue, Color.White)
        val earlier = Event(today.minus(1, DateTimeUnit.DAY), "A", Color.Blue, Color.White)
        composeRule.runOnIdle {
            store.setEvents(listOf(later, earlier))
            // Readable right away (no frame, no collector needed) and already in display order.
            assertEquals(listOf("A", "B"), store.eventsFlow.value.map { it.name })
        }
    }

    // --- helpers (independent of the production grid code) -----------------------------------

    /** Short day/month label in the device locale, e.g. "14.10." (de) or "10/14" (en-US). */
    private fun dayLabel(date: LocalDate): String {
        val locale = java.util.Locale.getDefault()
        val pattern = android.text.format.DateFormat.getBestDateTimePattern(locale, "dM")
        return java.text.SimpleDateFormat(pattern, locale)
            .format(java.util.GregorianCalendar(date.year, date.month.number - 1, date.day).time)
    }

    private fun rowStartOf(date: LocalDate, weekStart: DayOfWeek): LocalDate {
        val back = (date.dayOfWeek.ordinal - weekStart.ordinal + 7) % 7
        return date.minus(back, DateTimeUnit.DAY)
    }

    /** The 6 row-start dates of the month grid for [month]. */
    private fun monthRows(month: YearMonth, weekStart: DayOfWeek): List<LocalDate> {
        val first = rowStartOf(month.atDay(1), weekStart)
        return (0 until 6).map { first.plus(it * 7, DateTimeUnit.DAY) }
    }

    private fun javaIsoWeek(date: LocalDate): Int =
        java.time.LocalDate.of(date.year, date.month.number, date.day)
            .get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
}
