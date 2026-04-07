package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import java.time.LocalDate
import java.time.YearMonth

internal val today: LocalDate = LocalDate.now()
private val TodayBadgeShape = RoundedCornerShape(50)

/**
 * Renders a single calendar day cell.
 *
 * The cell shows:
 * - The day-of-month number (with a "today" badge when applicable)
 * - A list of events for the given date (as chips)
 *
 * If there are more than 3 events, the event list becomes vertically scrollable.
 *
 * @param modifier Modifier applied to the outer container.
 * @param calendarDay The day to render.
 * @param events Events belonging to [calendarDay.date].
 * @param shape Background/clip shape for the day cell.
 * @param visibleMonth The month currently displayed by the calendar (used to determine "today").
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 * @param onDaySelected Callback invoked when the day cell is tapped.
 */
@Composable
internal fun DayItem(
    modifier: Modifier = Modifier,
    calendarDay: CalendarDay,
    events: List<Event>,
    shape: Shape,
    visibleMonth: YearMonth,
    calendarStyle: CalendarStyle,
    onDaySelected: (calendarDay: CalendarDay) -> Unit
) {
    val isVisibleMonthCurrent = remember(visibleMonth) { visibleMonth == YearMonth.now() }
    val isToday = isVisibleMonthCurrent && calendarDay.date == today

    val defaultTextColor =
        if (calendarDay.isCurrentMonth) calendarStyle.dayItemTextColor
        else calendarStyle.weekDayInactiveTextColor

    val defaultFontStyle =
        if (calendarDay.isCurrentMonth) FontStyle.Normal else FontStyle.Italic

    val textColor = if (isToday) calendarStyle.currentDayTextColor else defaultTextColor
    val style = if (isToday) FontStyle.Normal else defaultFontStyle

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(calendarStyle.dayItemBackgroundColor, shape)
            .clip(shape)
            .clickable { onDaySelected(calendarDay) },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    modifier = Modifier
                        .then(
                            if (isToday) {
                                Modifier
                                    .clip(TodayBadgeShape)
                                    .background(calendarStyle.currentDayBackgroundColor)
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    text = calendarDay.date.dayOfMonth.toString(),
                    color = textColor,
                    fontStyle = style,
                    fontSize = calendarStyle.textUnit,
                    lineHeight = calendarStyle.textUnit
                )
            }

            val listModifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 4.dp)

            if (events.size > 3) {
                LazyColumn(
                    modifier = listModifier,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        items = events,
                        key = { it.id }
                    ) { event ->
                        EventChip(
                            text = event.name,
                            shapeColor = event.shapeColor,
                            textColor = event.effectiveTextColor
                        )
                    }
                }
            } else {
                Column(
                    modifier = listModifier,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    events.forEach { event ->
                        EventChip(
                            text = event.name,
                            shapeColor = event.shapeColor,
                            textColor = event.effectiveTextColor
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayItemPreview() {
    val previewToday = LocalDate.now()
    val events = listOf(
        Event(previewToday, "Cooking", shapeColor = Color(0xFFEF6C00), textColor = Color.White),
        Event(previewToday, "Board Games", shapeColor = Color(0xFF43A047), textColor = Color.White),
        Event(previewToday, "Volunteer", shapeColor = Color(0xFF3949AB), textColor = Color.White),
        Event(previewToday, "Movie Night", shapeColor = Color(0xFFFDD835), textColor = Color.Black),
        Event(previewToday, "Vacation", shapeColor = Color(0xFF039BE5), textColor = Color.White),
    )

    DayItem(
        modifier = Modifier.fillMaxSize(),
        calendarDay = CalendarDay(
            date = previewToday,
            isCurrentMonth = true,
            events = emptyList()
        ),
        events = events,
        shape = RoundedCornerShape(8.dp),
        visibleMonth = YearMonth.now(),
        calendarStyle = defaultCalendarStyle().copy(textUnit = 12.sp),
        onDaySelected = {}
    )
}