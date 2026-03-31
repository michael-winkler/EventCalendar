package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.WeekItemPosition

@Composable
fun WeekItem(
    modifier: Modifier = Modifier,
    weekNumber: Int,
    position: WeekItemPosition,
    calendarStyle: CalendarStyle
) {
    val shapes = rememberWeekItemShapes(outerRadius = 50.dp, innerRadius = 4.dp)
    val shape = shapes.forPosition(position)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .background(calendarStyle.weekItemBackgroundColor, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = weekNumber.toString(),
            color = calendarStyle.weekItemTextColor,
            fontSize = calendarStyle.textUnit
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeekItemPreview() {
    WeekItem(
        modifier = Modifier,
        weekNumber = 1,
        position = WeekItemPosition.Top,
        calendarStyle = defaultCalendarStyle()
    )
}