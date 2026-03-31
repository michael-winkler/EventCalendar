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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.MonthHeaderLayout
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    modifier: Modifier,
    calendarController: CalendarController,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    events: List<Event> = emptyList(),
    onDaySelected: (CalendarDay) -> Unit
) {
    val pagerState = calendarController.pagerState
    val scope = calendarController.scope
    val baseMonth = calendarController.baseMonth
    val basePage = calendarController.basePage

    val currentMonth by remember(pagerState, baseMonth, basePage) {
        derivedStateOf {
            baseMonth.plusMonths((pagerState.currentPage - basePage).toLong())
        }
    }

    // group + sort once
    val eventsByDate: Map<LocalDate, List<Event>> = remember(events) {
        events
            .groupBy { it.date }
            .mapValues { (_, list) ->
                list.sortedWith(
                    compareBy<Event> { it.timeRange?.startHour ?: Int.MAX_VALUE }
                        .thenBy { it.timeRange?.startMinute ?: Int.MAX_VALUE }
                        .thenBy { it.name }
                )
            }
    }

    // stable lookup
    val eventsForDate: (LocalDate) -> List<Event> = remember(eventsByDate) {
        { date -> eventsByDate[date].orEmpty() }
    }

    // ensure Unit (not Job)
    val onPrevMonth: () -> Unit = remember(scope, pagerState) {
        {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        }
    }
    val onNextMonth: () -> Unit = remember(scope, pagerState) {
        {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val constraintsScope = this
        val isLandscape = constraintsScope.maxWidth > constraintsScope.maxHeight

        val weekHeaderHeight = 32.dp
        val monthHeaderHeightPortrait = 48.dp
        val monthHeaderWidthLandscape = 48.dp

        if (!isLandscape) {
            val heightAfterMonthHeader =
                constraintsScope.maxHeight -
                        (if (calendarOptions.headerVisible) monthHeaderHeightPortrait else 0.dp)
            val gridHeight = (heightAfterMonthHeader - weekHeaderHeight).coerceAtLeast(0.dp)

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
                    baseMonth = baseMonth,
                    basePage = basePage,
                    pagerState = pagerState,
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
            val rightWidth =
                (constraintsScope.maxWidth - monthHeaderWidthLandscape).coerceAtLeast(0.dp)
            val gridHeight =
                (constraintsScope.maxHeight - weekHeaderHeight).coerceAtLeast(0.dp)

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
                        baseMonth = baseMonth,
                        basePage = basePage,
                        pagerState = pagerState,
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
    baseMonth: YearMonth,
    basePage: Int,
    pagerState: PagerState,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    currentMonth: YearMonth,
    weekHeaderHeight: Dp,
    gridHeight: Dp,
    eventsForDate: (LocalDate) -> List<Event>,
    onDaySelected: (CalendarDay) -> Unit
) {
    Column(modifier = modifier) {
        // WeekHeader stays unchanged (as you requested)
        WeekHeader(
            currentMonth = currentMonth,
            itemHeight = weekHeaderHeight,
            calendarOptions = calendarOptions,
            calendarStyle = calendarStyle
        )

        if (calendarOptions.calendarWeekVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight)
            ) {
                WeekNumberColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    yearMonth = currentMonth,
                    weekStart = calendarOptions.weekStart,
                    calendarStyle = calendarStyle
                )

                HorizontalPager(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(7f),
                    state = pagerState,
                    pageSize = PageSize.Fill,
                    beyondViewportPageCount = 1
                ) { page ->
                    val month = remember(page, baseMonth, basePage) {
                        baseMonth.plusMonths((page - basePage).toLong())
                    }

                    androidx.compose.runtime.key(month) {
                        MonthView(
                            yearMonth = month,
                            calendarOptions = calendarOptions.copy(calendarWeekVisible = false),
                            calendarStyle = calendarStyle,
                            eventsForDate = eventsForDate,
                            onDaySelected = onDaySelected
                        )
                    }
                }
            }
        } else {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight),
                state = pagerState,
                pageSize = PageSize.Fill,
                beyondViewportPageCount = 1
            ) { page ->
                val month = remember(page, baseMonth, basePage) {
                    baseMonth.plusMonths((page - basePage).toLong())
                }

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
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val today = LocalDate.now()
    CalendarScreen(
        modifier = Modifier.fillMaxSize(),
        calendarController = rememberCalendarController(),
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle(),
        events = listOf(
            Event(today, "Cooking", shapeColor = Color(0xFFEF6C00), textColor = Color.White),
            Event(today, "Board Games", shapeColor = Color(0xFF43A047), textColor = Color.White),
            Event(today, "Volunteer", shapeColor = Color(0xFF3949AB), textColor = Color.White),
            Event(today, "Movie Night", shapeColor = Color(0xFFFDD835), textColor = Color.Black),
            Event(today, "Vacation", shapeColor = Color(0xFF039BE5), textColor = Color.White),
        ),
        onDaySelected = {}
    )
}