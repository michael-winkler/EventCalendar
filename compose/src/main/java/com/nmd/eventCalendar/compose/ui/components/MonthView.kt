package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.DayCornerPosition
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.WeekItemPosition
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.shapes.rememberDayCornerShapes
import com.nmd.eventCalendar.compose.util.generateMonthDays
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

internal val PhoneLandscapeRowHeight = 90.dp

/**
 * Renders a month grid view (6 weeks x 7 days) or a single week if restricted.
 *
 * In phone landscape mode, each week row uses a fixed height ([PhoneLandscapeRowHeight]) so the
 * whole month can extend beyond the viewport and be scrolled by a parent container.
 *
 * @param yearMonth The month to display.
 * @param calendarOptions Calendar configuration options (week start, week numbers visibility, etc.).
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 * @param eventsForDate Provides the events for a given date.
 * @param onDaySelected Callback invoked when a day is tapped.
 * @param phoneLandscape If true, uses a fixed row height optimized for phone landscape layouts.
 */
@Composable
fun MonthView(
    yearMonth: YearMonth,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    eventsForDate: (LocalDate) -> List<Event>,
    onDaySelected: (calendarDay: CalendarDay) -> Unit,
    phoneLandscape: Boolean = false
) {
    val baseDays =
        remember(yearMonth, calendarOptions.weekStart, calendarOptions.isCurrentWeekOnly) {
            generateMonthDays(
                yearMonth = yearMonth,
                weekStart = calendarOptions.weekStart,
                eventsByDate = emptyMap(),
                isCurrentWeekOnly = calendarOptions.isCurrentWeekOnly
            )
        }

    val weeks: List<List<CalendarDay>> = remember(baseDays) { baseDays.chunked(7) }

    val weekNumbers = remember(weeks, calendarOptions.calendarWeekVisible) {
        if (!calendarOptions.calendarWeekVisible) emptyList()
        else weeks.map { week -> week.first().date.get(WeekFields.ISO.weekOfWeekBasedYear()) }
    }

    val cornerShapes = rememberDayCornerShapes(
        outerRadius = 16.dp,
        innerRadius = 4.dp
    )

    val columnModifier = if (calendarOptions.isCurrentWeekOnly) {
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    } else {
        Modifier.fillMaxSize()
    }

    Column(modifier = columnModifier) {
        weeks.forEachIndexed { weekIndex, week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        when {
                            calendarOptions.isCurrentWeekOnly -> Modifier.height(90.dp)
                            phoneLandscape -> Modifier.height(PhoneLandscapeRowHeight)
                            else -> Modifier.weight(1f)
                        }
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
                            .weight(1f),
                        weekNumber = weekNumber,
                        position = position,
                        calendarStyle = calendarStyle,
                        isSingle = calendarOptions.isCurrentWeekOnly
                    )
                }

                week.forEachIndexed { dayIndex, day ->
                    val corner = dayCornerFor(
                        row = weekIndex,
                        col = dayIndex,
                        lastRow = weeks.lastIndex
                    )

                    DayItem(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        calendarDay = day,
                        events = eventsForDate(day.date),
                        shape = cornerShapes.forPosition(
                            position = corner,
                            isFirstInSingleWeek = calendarOptions.isCurrentWeekOnly && dayIndex == 0,
                            isLastInSingleWeek = calendarOptions.isCurrentWeekOnly && dayIndex == 6
                        ),
                        visibleMonth = yearMonth,
                        calendarStyle = calendarStyle,
                        onDaySelected = onDaySelected
                    )
                }
            }
        }
    }
}

private fun dayCornerFor(row: Int, col: Int, lastRow: Int): DayCornerPosition = when {
    row == 0 && col == 0 -> DayCornerPosition.TopLeft
    row == 0 && col == 6 -> DayCornerPosition.TopRight
    row == lastRow && col == 0 -> DayCornerPosition.BottomLeft
    row == lastRow && col == 6 -> DayCornerPosition.BottomRight
    else -> DayCornerPosition.Default
}

@Preview(showBackground = true)
@Composable
fun MonthViewPreview() {
    val previewToday = LocalDate.now()

    MonthView(
        yearMonth = YearMonth.now(),
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle(),
        eventsForDate = { date ->
            if (date == previewToday) {
                listOf(
                    Event(
                        previewToday,
                        "Cooking",
                        shapeColor = Color(0xFFEF6C00),
                        textColor = Color.White
                    ),
                    Event(
                        previewToday,
                        "Board Games",
                        shapeColor = Color(0xFF43A047),
                        textColor = Color.White
                    ),
                    Event(
                        previewToday,
                        "Volunteer",
                        shapeColor = Color(0xFF3949AB),
                        textColor = Color.White
                    ),
                    Event(
                        previewToday,
                        "Movie Night",
                        shapeColor = Color(0xFFFDD835),
                        textColor = Color.Black
                    ),
                    Event(
                        previewToday,
                        "Vacation",
                        shapeColor = Color(0xFF039BE5),
                        textColor = Color.White
                    ),
                )
            } else emptyList()
        },
        onDaySelected = {},
        phoneLandscape = true
    )
}