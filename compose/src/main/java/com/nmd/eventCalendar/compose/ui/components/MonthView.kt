package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.CalendarWeekColumnWidth
import com.nmd.eventCalendar.compose.ui.config.calendarMonthGrid
import com.nmd.eventCalendar.compose.ui.config.calendarRow
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.shapes.rememberDayCornerShapes
import com.nmd.eventCalendar.compose.util.EventSegment
import com.nmd.eventCalendar.compose.util.assignEventLanes
import com.nmd.eventCalendar.compose.util.generateMonthDays
import com.nmd.eventCalendar.compose.util.isoWeekNumber
import com.nmd.eventCalendar.compose.util.mergeAdjacentEvents
import com.nmd.eventCalendar.compose.util.segmentsForWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Renders a month grid view (6 weeks x 7 days) or a single week if restricted.
 *
 * In phone landscape mode, each week row uses a fixed height ([com.nmd.eventCalendar.compose.ui.config.CalendarRowHeight]) so the
 * whole month can extend beyond the viewport and be scrolled by a parent container.
 *
 * @param yearMonth The month to display.
 * @param calendarOptions Calendar configuration options (week start, week numbers visibility, single week mode, etc.).
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 * @param eventsForDate Provides the events for a given date.
 * @param onDaySelected Callback invoked when a day is tapped.
 * @param phoneLandscape If true, uses a fixed row height optimized for phone landscape layouts.
 */
@Composable
internal fun MonthView(
    yearMonth: YearMonth,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    eventsForDate: (LocalDate) -> List<Event>,
    onDaySelected: (calendarDay: CalendarDay) -> Unit,
    phoneLandscape: Boolean = false
) {
    val baseDays =
        remember(
            yearMonth,
            calendarOptions.weekStart,
            calendarOptions.isCurrentWeekOnly,
            eventsForDate
        ) {
            generateMonthDays(
                yearMonth = yearMonth,
                weekStart = calendarOptions.weekStart,
                eventsByDate = emptyMap(),
                isCurrentWeekOnly = calendarOptions.isCurrentWeekOnly
            ).map { day ->
                day.copy(events = eventsForDate(day.date))
            }
        }

    val weeks: List<List<CalendarDay>> = remember(baseDays) { baseDays.chunked(7) }

    val weekNumbers = remember(weeks, calendarOptions.calendarWeekVisible) {
        if (!calendarOptions.calendarWeekVisible) emptyList()
        else weeks.map { week -> week.first().date.isoWeekNumber() }
    }

    // Distinct events across the whole visible grid get a stable lane so multi-day events keep the
    // same row across consecutive weeks and render as one continuous bar. Segments are then resolved
    // per week (a multi-day event crossing a week boundary yields one segment per week).
    val weekSegments: List<List<EventSegment>> = remember(weeks, calendarOptions.mergeAdjacentEvents) {
        val distinctEvents = weeks.flatten().flatMap { it.events }.distinctBy { it.id }
        // Optionally combine separate single-day, all-day events of the same type on consecutive
        // days into one continuous bar (see mergeAdjacentEvents for the safeguards).
        val renderEvents =
            if (calendarOptions.mergeAdjacentEvents) mergeAdjacentEvents(distinctEvents)
            else distinctEvents
        val lanes = assignEventLanes(renderEvents)
        weeks.map { week ->
            segmentsForWeek(
                weekDates = week.map { it.date },
                events = renderEvents
            ) { event -> lanes[event.id] ?: 0 }
        }
    }

    val cornerShapes = rememberDayCornerShapes(
        outerRadius = 16.dp,
        innerRadius = 4.dp
    )

    val weekRowWeight =
        if (calendarOptions.calendarWeekVisible && !phoneLandscape) 7f else 1f

    Column(
        modifier = Modifier.calendarMonthGrid(
            options = calendarOptions,
            isPhoneLandscape = phoneLandscape
        )
    ) {
        weeks.forEachIndexed { weekIndex, week ->
            val segments = weekSegments[weekIndex]

            Row(
                modifier = Modifier.calendarRow(
                    columnScope = this,
                    options = calendarOptions,
                    isPhoneLandscape = phoneLandscape
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (calendarOptions.calendarWeekVisible) {
                    val weekNumber = weekNumbers[weekIndex]
                    val position = when {
                        weeks.size == 1 -> WeekItemPosition.Middle
                        weekIndex == 0 -> WeekItemPosition.Top
                        weekIndex == weeks.lastIndex -> WeekItemPosition.Bottom
                        else -> WeekItemPosition.Middle
                    }

                    WeekItem(
                        modifier = Modifier
                            .fillMaxHeight()
                            .then(
                                if (phoneLandscape) Modifier.width(CalendarWeekColumnWidth)
                                else Modifier.weight(1f)
                            ),
                        weekNumber = weekNumber,
                        position = position,
                        calendarStyle = calendarStyle,
                        isSingle = calendarOptions.isCurrentWeekOnly
                    )
                }

                WeekRow(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(weekRowWeight),
                    week = week,
                    segments = segments,
                    weekIndex = weekIndex,
                    lastWeekIndex = weeks.lastIndex,
                    isSingleWeek = calendarOptions.isCurrentWeekOnly,
                    visibleMonth = yearMonth,
                    calendarStyle = calendarStyle,
                    cornerShapes = cornerShapes,
                    onDaySelected = onDaySelected
                )
            }
        }
    }
}

private fun monthPreviewEvents(previewToday: LocalDate): List<Event> = listOf(
    Event(previewToday, "Cooking", shapeColor = Color(0xFFEF6C00), textColor = Color.White),
    Event(previewToday, "Board Games", shapeColor = Color(0xFF43A047), textColor = Color.White),
    // Multi-day event spanning several days (and across a week boundary).
    Event(
        date = previewToday.minus(2, kotlinx.datetime.DateTimeUnit.DAY),
        name = "Vacation",
        shapeColor = Color(0xFF039BE5),
        textColor = Color.White,
        endDate = previewToday.plus(5, kotlinx.datetime.DateTimeUnit.DAY)
    ),
    Event(
        date = previewToday.plus(1, kotlinx.datetime.DateTimeUnit.DAY),
        name = "Conference",
        shapeColor = Color(0xFF3949AB),
        textColor = Color.White,
        endDate = previewToday.plus(3, kotlinx.datetime.DateTimeUnit.DAY)
    ),
)

@Preview(showBackground = true)
@Composable
internal fun MonthViewPreview() {
    val previewToday = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val events = remember(previewToday) { monthPreviewEvents(previewToday) }

    MonthView(
        yearMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle(),
        eventsForDate = { date -> events.filter { it.occursOn(date) } },
        onDaySelected = {},
        phoneLandscape = false
    )
}

@Preview(
    showBackground = true,
    widthDp = 740,
    heightDp = 360
)
@Composable
internal fun MonthViewPreviewLandscape() {
    val previewToday = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val events = remember(previewToday) { monthPreviewEvents(previewToday) }

    MonthView(
        yearMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle(),
        eventsForDate = { date -> events.filter { it.occursOn(date) } },
        onDaySelected = {},
        phoneLandscape = true
    )
}