package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.DayCornerPosition
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.shapes.DayCornerShapes
import com.nmd.eventCalendar.compose.util.EventSegment

private val LaneHeight = 16.dp
private val LaneSpacing = 2.dp
private val EventBarShapeRadius = 6.dp

// Inset around each day cell's background. Event bars use the same value at their real start/end so
// a bar sits exactly inside the day cell, while continuation edges reach the grid edge to bridge.
private val CellPadding = 2.dp

/**
 * Renders a full calendar week: the day-cell backgrounds, the day numbers, and the event lanes.
 *
 * Events are drawn as continuous horizontal bars that can span several day columns. Multi-day
 * events keep the same lane across the whole week (and across weeks, thanks to the lane assignment
 * performed at the month level), so a bar that runs from e.g. Saturday into the next week appears
 * as one object rather than duplicated single-day chips.
 *
 * The day-cell backgrounds form the click layer: taps anywhere in a column (including on an event
 * bar) select that day, matching the previous behaviour.
 *
 * @param modifier Modifier applied to the week container (usually carries the row height).
 * @param week The 7 [CalendarDay]s of this week, ordered by the configured week start.
 * @param segments Precomputed event segments for this week (see [EventSegment]).
 * @param weekIndex Index of this week within the grid (used for corner rounding).
 * @param lastWeekIndex Index of the last week within the grid (used for corner rounding).
 * @param isSingleWeek Whether the calendar is rendering a single week only.
 * @param visibleMonth The month currently displayed (used to determine "today").
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 * @param cornerShapes Prebuilt day-cell corner shapes.
 * @param onDaySelected Callback invoked when a day column is tapped.
 */
@Composable
internal fun WeekRow(
    modifier: Modifier = Modifier,
    week: List<CalendarDay>,
    segments: List<EventSegment>,
    weekIndex: Int,
    lastWeekIndex: Int,
    isSingleWeek: Boolean,
    visibleMonth: YearMonth,
    calendarStyle: CalendarStyle,
    cornerShapes: DayCornerShapes,
    onDaySelected: (calendarDay: CalendarDay) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background + click layer: one rounded cell per day column.
        Row(modifier = Modifier.fillMaxSize()) {
            week.forEachIndexed { dayIndex, day ->
                val corner = dayCornerFor(
                    row = weekIndex,
                    col = dayIndex,
                    lastRow = lastWeekIndex
                )
                val shape = cornerShapes.forPosition(
                    position = corner,
                    isFirstInSingleWeek = isSingleWeek && dayIndex == 0,
                    isLastInSingleWeek = isSingleWeek && dayIndex == 6
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(CellPadding)
                        .background(calendarStyle.dayItemBackgroundColor, shape)
                        .clip(shape)
                        .clickable { onDaySelected(day) }
                )
            }
        }

        // Content layer: day numbers on top, event lanes below. This layer is not interactive, so
        // taps fall through to the background click layer above.
        Column(
            modifier = Modifier
                .fillMaxSize()
                // No horizontal padding: the day-number/lane columns must use the exact same 7-column
                // grid as the background cells, otherwise bars drift out of their day cell.
                .padding(vertical = 4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    DayNumberCell(
                        modifier = Modifier.weight(1f),
                        calendarDay = day,
                        visibleMonth = visibleMonth,
                        calendarStyle = calendarStyle
                    )
                }
            }

            EventLanes(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 4.dp),
                segments = segments,
                calendarStyle = calendarStyle
            )
        }
    }
}

/**
 * Lays out the event [segments] into stacked lanes, capping the number of visible lanes to what
 * fits in the available height and collapsing the remainder into a per-column "+N" indicator.
 */
@Composable
private fun EventLanes(
    modifier: Modifier,
    segments: List<EventSegment>,
    calendarStyle: CalendarStyle
) {
    if (segments.isEmpty()) {
        Box(modifier = modifier)
        return
    }

    BoxWithConstraints(modifier = modifier) {
        val laneStride = LaneHeight + LaneSpacing
        val maxLanes = ((maxHeight + LaneSpacing).value / laneStride.value).toInt().coerceAtLeast(0)
        if (maxLanes == 0) return@BoxWithConstraints

        // Lanes are assigned globally across the whole grid so a multi-day bar keeps the same row
        // across weeks. For the visible-row/overflow budget we only look at the lanes actually
        // present in THIS week and compact them (gaps removed): otherwise a bar that ended up on a
        // high global lane could waste blank rows above it - or, on a short row, collapse into "+N"
        // (or vanish) even in a week where it is the only event.
        val presentLanes = remember(segments) { segments.map { it.lane }.distinct().sorted() }
        val neededLanes = presentLanes.size
        val overflow = neededLanes > maxLanes
        // Reserve a row for the "+N" indicators only when at least one real lane still remains
        // visible; otherwise (a row that fits a single lane) show that top lane instead of hiding
        // everything.
        val reserveOverflowRow = overflow && maxLanes >= 2
        val visibleRowCount =
            if (reserveOverflowRow) maxLanes - 1 else minOf(neededLanes, maxLanes)

        val visibleLanes = remember(presentLanes, visibleRowCount) {
            presentLanes.take(visibleRowCount)
        }
        val segmentsByRow = remember(segments, visibleLanes) {
            visibleLanes.map { lane -> segments.filter { it.lane == lane } }
        }

        val hiddenPerColumn = remember(segments, visibleLanes, reserveOverflowRow) {
            if (!reserveOverflowRow) IntArray(0)
            else {
                val shown = visibleLanes.toHashSet()
                IntArray(7) { column ->
                    segments.count { it.lane !in shown && column in it.startColumn..it.endColumn }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(LaneSpacing)) {
            segmentsByRow.forEach { rowSegments ->
                LaneRow(segments = rowSegments)
            }
            if (reserveOverflowRow) {
                OverflowRow(hiddenPerColumn = hiddenPerColumn, calendarStyle = calendarStyle)
            }
        }
    }
}

/**
 * Renders a single lane as a row of 7 equal columns, where each event segment occupies a contiguous,
 * gap-free span of columns. Uncovered columns are filled with weighted spacers so that every lane
 * stays perfectly aligned to the day grid.
 */
@Composable
private fun LaneRow(
    segments: List<EventSegment>
) {
    val byStartColumn = remember(segments) { segments.associateBy { it.startColumn } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(LaneHeight)
    ) {
        var column = 0
        while (column <= 6) {
            val segment = byStartColumn[column]
            if (segment != null) {
                val span = (segment.endColumn - segment.startColumn + 1).coerceAtLeast(1)
                EventBar(
                    modifier = Modifier.weight(span.toFloat()),
                    segment = segment
                )
                column = segment.endColumn + 1
            } else {
                Spacer(modifier = Modifier.weight(1f))
                column++
            }
        }
    }
}

/**
 * A single continuous event bar. Corners are rounded only on the ends that represent the real start
 * or end of the event; edges that continue into an adjacent week stay flat so the bar reads as one
 * object spanning multiple weeks.
 */
@Composable
private fun EventBar(
    modifier: Modifier,
    segment: EventSegment
) {
    val startRadius = if (segment.continuesBefore) 0.dp else EventBarShapeRadius
    val endRadius = if (segment.continuesAfter) 0.dp else EventBarShapeRadius
    val shape = remember(startRadius, endRadius) {
        RoundedCornerShape(
            topStart = startRadius,
            bottomStart = startRadius,
            topEnd = endRadius,
            bottomEnd = endRadius
        )
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(
                start = if (segment.continuesBefore) 0.dp else CellPadding,
                end = if (segment.continuesAfter) 0.dp else CellPadding
            )
            .clip(shape)
            .background(segment.event.shapeColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            text = segment.event.name,
            color = segment.event.effectiveTextColor,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Renders the "+N more" indicators for events that did not fit into the visible lanes, one per day
 * column so the count stays associated with the correct day.
 */
@Composable
private fun OverflowRow(
    hiddenPerColumn: IntArray,
    calendarStyle: CalendarStyle
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(LaneHeight)
    ) {
        for (column in 0..6) {
            val count = hiddenPerColumn.getOrElse(column) { 0 }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                if (count > 0) {
                    Text(
                        text = "+$count",
                        color = calendarStyle.dayItemTextColor,
                        fontSize = 9.sp,
                        lineHeight = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
    }
}

/**
 * Determines the corner rounding position of a day cell based on its position in the month grid.
 */
internal fun dayCornerFor(row: Int, col: Int, lastRow: Int): DayCornerPosition = when (row) {
    0 if col == 0 -> DayCornerPosition.TopLeft
    0 if col == 6 -> DayCornerPosition.TopRight
    lastRow if col == 0 -> DayCornerPosition.BottomLeft
    lastRow if col == 6 -> DayCornerPosition.BottomRight
    else -> DayCornerPosition.Default
}
