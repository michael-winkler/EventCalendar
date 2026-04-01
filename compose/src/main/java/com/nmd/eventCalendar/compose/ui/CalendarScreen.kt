package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.MonthHeaderLayout
import com.nmd.eventCalendar.compose.ui.components.MonthHeader
import com.nmd.eventCalendar.compose.ui.components.MonthView
import com.nmd.eventCalendar.compose.ui.components.WeekHeader
import com.nmd.eventCalendar.compose.ui.components.WeekNumberColumn
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.controller.CalendarController
import com.nmd.eventCalendar.compose.ui.controller.rememberCalendarController
import com.nmd.eventCalendar.compose.ui.events.CalendarEventsStore
import com.nmd.eventCalendar.compose.ui.events.PreviewCalendarEventsStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    modifier: Modifier,
    calendarController: CalendarController,
    eventsStore: CalendarEventsStore,
    onDaySelected: (CalendarDay) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle
) {
    val effectiveEvents = eventsStore.events()

    val pagerState = calendarController.pagerState
    val currentMonth by remember(calendarController, pagerState) {
        derivedStateOf { calendarController.pageToMonth(pagerState.currentPage) }
    }

    val onMonthChangeState = rememberUpdatedState(onMonthChange)
    LaunchedEffect(pagerState, calendarController) {
        snapshotFlow { pagerState.currentPage to pagerState.isScrollInProgress }
            .filter { (_, scrolling) -> !scrolling }
            .map { (page, _) -> calendarController.pageToMonth(page) }
            .distinctUntilChanged()
            .collect { month -> onMonthChangeState.value(month) }
    }

    val eventsByDate: Map<LocalDate, List<Event>> = remember(effectiveEvents) {
        effectiveEvents
            .groupBy { it.date }
            .mapValues { (_, list) ->
                list.sortedWith(
                    compareBy<Event> { it.timeRange?.startHour ?: Int.MAX_VALUE }
                        .thenBy { it.timeRange?.startMinute ?: Int.MAX_VALUE }
                        .thenBy { it.name }
                )
            }
    }

    val eventsForDate: (LocalDate) -> List<Event> = remember(eventsByDate) {
        { date -> eventsByDate[date].orEmpty() }
    }

    val onPrevMonth: () -> Unit = remember(calendarController) {
        { calendarController.goToPreviousMonth() }
    }
    val onNextMonth: () -> Unit = remember(calendarController) {
        { calendarController.goToNextMonth() }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight
        val weekHeaderHeight = 32.dp
        val monthHeaderHeightPortrait = 48.dp
        val monthHeaderWidthLandscape = 48.dp

        if (!isLandscape) {
            val gridHeight = remember(maxHeight, calendarOptions.headerVisible) {
                val heightAfterHeader =
                    maxHeight - (if (calendarOptions.headerVisible) monthHeaderHeightPortrait else 0.dp)
                (heightAfterHeader - weekHeaderHeight).coerceAtLeast(0.dp)
            }
            Column(Modifier.fillMaxSize()) {
                if (calendarOptions.headerVisible) {
                    MonthHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = onPrevMonth,
                        onNextMonth = onNextMonth,
                        calendarStyle = calendarStyle,
                        layout = MonthHeaderLayout.TopBar
                    )
                }
                CalendarPagerSection(
                    pagerState = pagerState,
                    calendarController = calendarController,
                    calendarOptions = calendarOptions,
                    calendarStyle = calendarStyle,
                    currentMonth = currentMonth,
                    weekHeaderHeight = weekHeaderHeight,
                    gridHeight = gridHeight,
                    eventsForDate = eventsForDate,
                    modifier = Modifier.fillMaxSize(),
                    onDaySelected = onDaySelected
                )
            }
        } else {
            val rightWidth = remember(maxWidth) {
                (maxWidth - monthHeaderWidthLandscape).coerceAtLeast(0.dp)
            }
            val gridHeight = remember(maxHeight) {
                (maxHeight - weekHeaderHeight).coerceAtLeast(0.dp)
            }
            Row(Modifier.fillMaxSize()) {
                if (calendarOptions.headerVisible) {
                    Column(
                        modifier = Modifier
                            .width(monthHeaderWidthLandscape)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        MonthHeader(
                            currentMonth = currentMonth,
                            onPreviousMonth = onPrevMonth,
                            onNextMonth = onNextMonth,
                            calendarStyle = calendarStyle,
                            layout = MonthHeaderLayout.SideBar
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .width(rightWidth)
                        .fillMaxHeight()
                ) {
                    CalendarPagerSection(
                        pagerState = pagerState,
                        calendarController = calendarController,
                        calendarOptions = calendarOptions,
                        calendarStyle = calendarStyle,
                        currentMonth = currentMonth,
                        weekHeaderHeight = weekHeaderHeight,
                        gridHeight = gridHeight,
                        eventsForDate = eventsForDate,
                        modifier = Modifier.fillMaxSize(),
                        onDaySelected = onDaySelected
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarPagerSection(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    calendarController: CalendarController,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    currentMonth: YearMonth,
    weekHeaderHeight: Dp,
    gridHeight: Dp,
    eventsForDate: (LocalDate) -> List<Event>,
    onDaySelected: (CalendarDay) -> Unit
) {
    Column(modifier = modifier) {
        WeekHeader(
            currentMonth = currentMonth,
            itemHeight = weekHeaderHeight,
            calendarOptions = calendarOptions,
            calendarStyle = calendarStyle
        )

        val pagerModifier = remember(gridHeight) {
            Modifier
                .fillMaxWidth()
                .height(gridHeight)
        }

        if (calendarOptions.calendarWeekVisible) {
            Row(
                modifier = remember(gridHeight) {
                    Modifier
                        .fillMaxWidth()
                        .height(gridHeight)
                }
            ) {
                WeekNumberColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    yearMonth = currentMonth,
                    weekStart = calendarOptions.weekStart,
                    calendarStyle = calendarStyle
                )
                MonthPager(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(7f),
                    pagerState = pagerState,
                    calendarController = calendarController,
                    calendarOptions = calendarOptions.copy(calendarWeekVisible = false),
                    calendarStyle = calendarStyle,
                    eventsForDate = eventsForDate,
                    onDaySelected = onDaySelected
                )
            }
        } else {
            MonthPager(
                modifier = pagerModifier,
                pagerState = pagerState,
                calendarController = calendarController,
                calendarOptions = calendarOptions,
                calendarStyle = calendarStyle,
                eventsForDate = eventsForDate,
                onDaySelected = onDaySelected
            )
        }
    }
}

@Composable
private fun MonthPager(
    modifier: Modifier,
    pagerState: PagerState,
    calendarController: CalendarController,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    eventsForDate: (LocalDate) -> List<Event>,
    onDaySelected: (CalendarDay) -> Unit
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSize = PageSize.Fill,
        beyondViewportPageCount = 1
    ) { page ->
        val month = calendarController.pageToMonth(page)
        androidx.compose.runtime.key(month) {
            MonthView(
                yearMonth = month,
                calendarOptions = calendarOptions,
                calendarStyle = calendarStyle,
                eventsForDate = eventsForDate,
                onDaySelected = onDaySelected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val today = LocalDate.now()

    val store = remember {
        PreviewCalendarEventsStore(
            initialEvents = listOf(
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
                Event(today, "Vacation", shapeColor = Color(0xFF039BE5), textColor = Color.White)
            )
        )
    }

    CalendarScreen(
        modifier = Modifier.fillMaxSize(),
        calendarController = rememberCalendarController(defaultCalendarOptions()),
        eventsStore = store,
        onDaySelected = {},
        onMonthChange = {},
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle()
    )
}