package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.DayCornerPosition
import com.nmd.eventCalendar.compose.model.Event
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun DayItem(
    calendarDay: CalendarDay,
    corner: DayCornerPosition,
    visibleMonth: YearMonth,
    calendarStyle: CalendarStyle,
    onDaySelected: (calendarDay: CalendarDay) -> Unit
) {
    val outerRadius = 16.dp
    val innerRadius = 4.dp

    val shape = when (corner) {
        DayCornerPosition.TopLeft ->
            RoundedCornerShape(outerRadius, innerRadius, innerRadius, innerRadius)

        DayCornerPosition.TopRight ->
            RoundedCornerShape(innerRadius, outerRadius, innerRadius, innerRadius)

        DayCornerPosition.BottomLeft ->
            RoundedCornerShape(innerRadius, innerRadius, innerRadius, outerRadius)

        DayCornerPosition.BottomRight ->
            RoundedCornerShape(innerRadius, innerRadius, outerRadius, innerRadius)

        DayCornerPosition.Default ->
            RoundedCornerShape(innerRadius)
    }

    val today = remember { LocalDate.now() }
    val isVisibleMonthCurrent = visibleMonth == YearMonth.now()
    val isToday = isVisibleMonthCurrent && calendarDay.date == today

    val defaultTextColor =
        if (calendarDay.isCurrentMonth) calendarStyle.dayItemTextColor
        else calendarStyle.weekDayInactiveTextColor

    val defaultFontStyle =
        if (calendarDay.isCurrentMonth) FontStyle.Normal else FontStyle.Italic

    // Voraussetzung: events sind idealerweise schon vorsortiert (z.B. in CalendarScreen beim groupBy)
    val dayEvents = calendarDay.events

    Box(
        modifier = Modifier
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
            // Day number (fixed area)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(23.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                if (isToday) {
                    Box(
                        modifier = Modifier
                            .height(23.dp)
                            .defaultMinSize(minWidth = 24.dp)
                            .clip(RoundedCornerShape(size = 50f))
                            .background(calendarStyle.currentDayBackgroundColor)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = calendarDay.date.dayOfMonth.toString(),
                            color = calendarStyle.currentDayTextColor,
                            fontSize = calendarStyle.textUnit
                        )
                    }
                } else {
                    Text(
                        text = calendarDay.date.dayOfMonth.toString(),
                        color = defaultTextColor,
                        fontStyle = defaultFontStyle,
                        fontSize = calendarStyle.textUnit
                    )
                }
            }

            // Scrollable events area = remaining space
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                userScrollEnabled = true
            ) {
                items(
                    items = dayEvents,
                    key = { e -> "${e.date}-${e.name}-${e.timeRange?.startHour}-${e.timeRange?.startMinute}" }
                ) { event ->
                    EventChip(
                        text = event.name,
                        shapeColor = event.shapeColor,
                        textColor = event.textColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayItemPreview() {
    val today = LocalDate.now()
    DayItem(
        calendarDay = CalendarDay(
            date = today,
            isCurrentMonth = true,
            events = listOf(
                Event(today, "Cooking", shapeColor = Color(0xFFEF6C00), textColor = Color.White),
                Event(
                    today,
                    "Board Games",
                    shapeColor = Color(0xFF43A047),
                    textColor = Color.White
                ),
                Event(today, "Volunteer", shapeColor = Color(0xFF3949AB), textColor = Color.White),
                Event(
                    today,
                    "Movie Night",
                    shapeColor = Color(0xFFFDD835),
                    textColor = Color.Black
                ),
                Event(today, "Vacation", shapeColor = Color(0xFF039BE5), textColor = Color.White),
            )
        ),
        corner = DayCornerPosition.Default,
        visibleMonth = YearMonth.now(),
        calendarStyle = defaultCalendarStyle().copy(textUnit = 12.sp),
        onDaySelected = {}
    )
}