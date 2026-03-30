package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.DayCornerPosition
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(calendarStyle.dayItemBackgroundColor, shape)
            .clip(shape)
            .clickable {
                onDaySelected(calendarDay)
            },
        contentAlignment = Alignment.TopCenter
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
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
                        fontSize = calendarStyle.fontsize
                    )
                }
            } else {
                Text(
                    text = calendarDay.date.dayOfMonth.toString(),
                    color = defaultTextColor,
                    fontStyle = defaultFontStyle,
                    fontSize = calendarStyle.fontsize
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayItemPreview() {
    DayItem(
        calendarDay = CalendarDay(
            date = LocalDate.now(),
            isCurrentMonth = true
        ),
        corner = DayCornerPosition.Default,
        visibleMonth = YearMonth.now(),
        calendarStyle = defaultCalendarStyle(),
        onDaySelected = {}
    )
}