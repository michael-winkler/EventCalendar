package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

internal val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
private val TodayBadgeShape = RoundedCornerShape(50)

/**
 * Renders the day-of-month number (with a "today" badge when applicable) for a single day column.
 *
 * The number is rendered on top of the day-cell background provided by [WeekRow]; the actual events
 * are drawn separately as continuous lanes so that multi-day events can span several day columns.
 *
 * @param modifier Modifier applied to the number container (typically a column weight).
 * @param calendarDay The day to render.
 * @param visibleMonth The month currently displayed by the calendar (used to determine "today").
 * @param calendarStyle Styling configuration (colors, typography sizes, etc.).
 */
@Composable
internal fun DayNumberCell(
    modifier: Modifier = Modifier,
    calendarDay: CalendarDay,
    visibleMonth: YearMonth,
    calendarStyle: CalendarStyle
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
        modifier = modifier.fillMaxWidth(),
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
            text = calendarDay.date.day.toString(),
            color = textColor,
            fontStyle = style,
            fontSize = calendarStyle.textUnit,
            lineHeight = calendarStyle.textUnit
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun DayNumberCellPreview() {
    DayNumberCell(
        modifier = Modifier.fillMaxWidth(),
        calendarDay = CalendarDay(
            date = today,
            isCurrentMonth = true,
            events = emptyList()
        ),
        visibleMonth = YearMonth.now(),
        calendarStyle = defaultCalendarStyle().copy(textUnit = 12.sp)
    )
}
