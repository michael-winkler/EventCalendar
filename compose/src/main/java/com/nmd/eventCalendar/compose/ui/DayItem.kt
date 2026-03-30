package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
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
    calendarStyle: CalendarStyle
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

    // Text-Farbe & Style für normale Tage
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
            .clickable { /* TODO */ },
        contentAlignment = Alignment.TopCenter
    ) {
        if (isToday) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clip(CircleShape)
                    .background(calendarStyle.currentDayBackgroundColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
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
                modifier = Modifier.padding(top = 2.dp),
                text = calendarDay.date.dayOfMonth.toString(),
                color = defaultTextColor,
                fontStyle = defaultFontStyle,
                fontSize = calendarStyle.fontsize
            )
        }
    }
}