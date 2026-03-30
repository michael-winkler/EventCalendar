package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.DayCornerPosition

@Composable
fun DayItem(
    calendarDay: CalendarDay,
    corner: DayCornerPosition,
    calendarStyle: CalendarStyle
) {
    val outerRadius = 16.dp
    val innerRadius = 4.dp

    val shape = when (corner) {
        DayCornerPosition.TopLeft ->
            RoundedCornerShape(
                topStart = outerRadius,
                topEnd = innerRadius,
                bottomEnd = innerRadius,
                bottomStart = innerRadius
            )

        DayCornerPosition.TopRight ->
            RoundedCornerShape(
                topStart = innerRadius,
                topEnd = outerRadius,
                bottomEnd = innerRadius,
                bottomStart = innerRadius
            )

        DayCornerPosition.BottomLeft ->
            RoundedCornerShape(
                topStart = innerRadius,
                topEnd = innerRadius,
                bottomEnd = innerRadius,
                bottomStart = outerRadius
            )

        DayCornerPosition.BottomRight ->
            RoundedCornerShape(
                topStart = innerRadius,
                topEnd = innerRadius,
                bottomEnd = outerRadius,
                bottomStart = innerRadius
            )

        DayCornerPosition.Default ->
            RoundedCornerShape(innerRadius)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(calendarStyle.dayItemBackgroundColor, shape)
            .clip(shape)
            .clickable(
                onClick = {
                    // TODO
                }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = calendarDay.date.dayOfMonth.toString(),
            color = if (calendarDay.isCurrentMonth) calendarStyle.dayItemTextColor else calendarStyle.weekDayInactiveTextColor,
            fontStyle = if (calendarDay.isCurrentMonth) FontStyle.Normal else FontStyle.Italic,
            fontSize = calendarStyle.fontsize
        )
    }
}