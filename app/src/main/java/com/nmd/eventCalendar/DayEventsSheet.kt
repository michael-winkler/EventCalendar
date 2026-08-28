package com.nmd.eventCalendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.util.toStringRes
import com.nmd.eventCalendarSample.R
import kotlinx.datetime.number

@Composable
fun DayEventsSheetContent(
    calendarDay: CalendarDay,
    calendarStyle: CalendarStyle
) {
    val dateText =
        "${calendarDay.date.day}.${calendarDay.date.month.number}.${calendarDay.date.year} (${calendarDay.events.size})"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dateText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = calendarStyle.dayItemTextColor
        )

        if (calendarDay.events.isEmpty()) {
            Text(
                text = stringResource(R.string.event_calendar_no_events),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                color = calendarStyle.dayItemTextColor.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(calendarDay.events) { event ->
                    EventItem(event)
                }
            }
        }
    }
}

@Composable
private fun EventItem(event: Event) {
    // For a multi-day event, show its full range (e.g. "Mon 1.9. – Wed 3.9.") so tapping any day of
    // the span makes clear it belongs to the whole event, not just the tapped day.
    val timeRange = event.timeRange
    val subtitle: String? = when {
        event.isMultiDay -> {
            val start = event.date
            val end = event.lastDate
            val startWeekday = stringResource(start.dayOfWeek.toStringRes())
            val endWeekday = stringResource(end.dayOfWeek.toStringRes())
            "$startWeekday ${start.day}.${start.month.number}. – " +
                "$endWeekday ${end.day}.${end.month.number}."
        }

        timeRange != null ->
            "${timeRange.getFormattedStart()} – ${timeRange.getFormattedEnd()}"

        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = event.shapeColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = event.name,
                color = event.effectiveTextColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = event.effectiveTextColor.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
