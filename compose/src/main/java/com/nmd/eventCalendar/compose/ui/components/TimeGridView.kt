package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.util.toStringRes
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * A time-based grid view that displays events for a list of days.
 *
 * Events are positioned vertically based on their start time and height is determined by duration.
 * Overlapping events within the same day are automatically resolved to share horizontal space.
 *
 * @param modifier Modifier applied to the root layout.
 * @param days List of dates to display as columns.
 * @param eventsByDate Map of events keyed by their date.
 * @param calendarStyle Styling configuration for colors and typography.
 * @param hourHeight Height of a single hour row.
 * @param onDaySelected Callback triggered when a day/background is selected.
 * @param onEventSelected Callback triggered when a specific event is selected.
 */
@Composable
internal fun TimeGridView(
    modifier: Modifier = Modifier,
    days: List<LocalDate>,
    eventsByDate: Map<LocalDate, List<Event>>,
    calendarStyle: CalendarStyle = defaultCalendarStyle(),
    hourHeight: Dp = 64.dp,
    onDaySelected: (LocalDate) -> Unit = {},
    onEventSelected: (Event) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        TimeGridHeader(days = days, calendarStyle = calendarStyle)

        Row(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            TimeLabelsColumn(hourHeight = hourHeight, calendarStyle = calendarStyle)

            Box(modifier = Modifier.weight(1f)) {
                TimeGridBackground(hourHeight = hourHeight)

                Row(modifier = Modifier.fillMaxSize()) {
                    days.forEach { date ->
                        DayTimeColumn(
                            modifier = Modifier.weight(1f),
                            date = date,
                            events = eventsByDate[date].orEmpty(),
                            hourHeight = hourHeight,
                            onDaySelected = onDaySelected,
                            onEventSelected = onEventSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeGridHeader(
    days: List<LocalDate>,
    calendarStyle: CalendarStyle
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)) {
        Spacer(modifier = Modifier.width(56.dp))
        days.forEach { date ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(date.dayOfWeek.toStringRes()).uppercase(),
                    fontSize = 10.sp,
                    color = calendarStyle.defaultWeekDayTextColor
                )
                Text(
                    text = "${date.day}/${date.month.number}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = calendarStyle.defaultWeekDayTextColor
                )
            }
        }
    }
}

@Composable
private fun TimeLabelsColumn(
    hourHeight: Dp,
    calendarStyle: CalendarStyle
) {
    Column(modifier = Modifier.width(56.dp)) {
        repeat(24) { hour ->
            Box(
                modifier = Modifier
                    .height(hourHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "%02d:00".format(hour),
                    fontSize = 10.sp,
                    color = calendarStyle.defaultWeekDayTextColor.copy(alpha = 0.6f),
                    modifier = Modifier.offset(y = (-6).dp)
                )
            }
        }
    }
}

@Composable
private fun TimeGridBackground(
    hourHeight: Dp,
    lineColor: Color = Color.LightGray.copy(alpha = 0.5f)
) {
    Column {
        repeat(24) {
            Box(
                modifier = Modifier
                    .height(hourHeight)
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            )
        }
    }
}

@Composable
private fun DayTimeColumn(
    modifier: Modifier,
    date: LocalDate,
    events: List<Event>,
    hourHeight: Dp,
    onDaySelected: (LocalDate) -> Unit,
    onEventSelected: (Event) -> Unit
) {
    val positionedEvents = remember(events) { resolveOverlaps(events) }

    Layout(
        content = {
            positionedEvents.forEach { positioned ->
                TimeEventChip(
                    event = positioned.event,
                    onClick = { onEventSelected(positioned.event) }
                )
            }
        },
        modifier = modifier
            .fillMaxHeight()
            .clickable { onDaySelected(date) }
            .drawBehind {
                // Draw vertical line on the left
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) { measurables, constraints ->
        val hourHeightPx = hourHeight.toPx()
        val placeables = measurables.mapIndexed { index, measurable ->
            val positioned = positionedEvents[index]
            val durationMinutes = positioned.event.timeRange!!.getDurationMinutes()
            val height = (durationMinutes / 60f * hourHeightPx).toInt()
            val width = (constraints.maxWidth / positioned.totalColumns)

            measurable.measure(
                constraints.copy(
                    minWidth = width,
                    maxWidth = width,
                    minHeight = height,
                    maxHeight = height
                )
            )
        }

        layout(constraints.maxWidth, (24 * hourHeightPx).toInt()) {
            placeables.forEachIndexed { index, placeable ->
                val positioned = positionedEvents[index]
                val startMinutes =
                    positioned.event.timeRange!!.startHour * 60 + positioned.event.timeRange.startMinute
                val y = (startMinutes / 60f * hourHeightPx).toInt()
                val x = positioned.columnIndex * (constraints.maxWidth / positioned.totalColumns)
                placeable.placeRelative(x, y)
            }
        }
    }
}

@Composable
private fun TimeEventChip(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(1.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(event.shapeColor)
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text = event.name,
            color = event.effectiveTextColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (event.timeRange != null) {
            Text(
                text = "${event.timeRange.getFormattedStart()} - ${event.timeRange.getFormattedEnd()}",
                color = event.effectiveTextColor.copy(alpha = 0.8f),
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

private data class PositionedEvent(
    val event: Event,
    val columnIndex: Int,
    val totalColumns: Int
)

private fun resolveOverlaps(events: List<Event>): List<PositionedEvent> {
    val eventsWithTime = events.filter { it.timeRange != null && it.timeRange.isValid() }
    if (eventsWithTime.isEmpty()) return emptyList()

    val sorted = eventsWithTime.sortedWith(
        compareBy(
        { it.timeRange!!.startHour * 60 + it.timeRange.startMinute },
        { -it.timeRange!!.getDurationMinutes() }
    ))

    val clusters = mutableListOf<MutableList<Event>>()
    var clusterMaxEnd = -1

    for (event in sorted) {
        val timeRange = event.timeRange!!
        val eventStart = timeRange.startHour * 60 + timeRange.startMinute
        val eventEnd = timeRange.endHour * 60 + timeRange.endMinute

        if (clusters.isEmpty() || eventStart >= clusterMaxEnd) {
            clusters.add(mutableListOf(event))
            clusterMaxEnd = eventEnd
        } else {
            clusters.last().add(event)
            clusterMaxEnd = maxOf(clusterMaxEnd, eventEnd)
        }
    }

    val result = mutableListOf<PositionedEvent>()
    for (cluster in clusters) {
        val columns = mutableListOf<MutableList<Event>>()
        for (event in cluster) {
            var placed = false
            for (column in columns) {
                if (!overlapsWithColumn(event, column)) {
                    column.add(event)
                    placed = true
                    break
                }
            }
            if (!placed) {
                columns.add(mutableListOf(event))
            }
        }

        for (columnIndex in columns.indices) {
            for (event in columns[columnIndex]) {
                result.add(PositionedEvent(event, columnIndex, columns.size))
            }
        }
    }
    return result
}

private fun overlapsWithColumn(event: Event, column: List<Event>): Boolean {
    return column.any { it.timeRange!!.overlapsWith(event.timeRange!!) }
}

@Preview(showBackground = true)
@Composable
internal fun TimeGridViewPreview() {
    val days = (0 until 7).map { LocalDate(2025, 10, 13 + it) }
    val events = listOf(
        Event(
            days[0],
            "Meeting",
            Color.Blue,
            Color.White,
            timeRange = com.nmd.eventCalendar.compose.model.EventTimeRange(9, 0, 10, 30)
        ),
        Event(
            days[0],
            "Workshop",
            Color.Red,
            Color.White,
            timeRange = com.nmd.eventCalendar.compose.model.EventTimeRange(10, 0, 12, 0)
        ),
        Event(
            days[1],
            "Lunch",
            Color.Green,
            Color.Black,
            timeRange = com.nmd.eventCalendar.compose.model.EventTimeRange(12, 0, 13, 0)
        )
    )
    val eventsByDate = events.groupBy { it.date }

    TimeGridView(
        days = days,
        eventsByDate = eventsByDate
    )
}
