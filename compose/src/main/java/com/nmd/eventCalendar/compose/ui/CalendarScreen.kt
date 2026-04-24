package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.model.MonthHeaderLayout
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventCalendar.compose.ui.components.MonthHeader
import com.nmd.eventCalendar.compose.ui.components.MonthView
import com.nmd.eventCalendar.compose.ui.components.WeekHeader
import com.nmd.eventCalendar.compose.ui.components.WeekNumberColumn
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.CalendarWeekColumnWidth
import com.nmd.eventCalendar.compose.ui.config.calendarBody
import com.nmd.eventCalendar.compose.ui.config.calendarMonthGrid
import com.nmd.eventCalendar.compose.ui.config.calendarRoot
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.controller.CalendarController
import com.nmd.eventCalendar.compose.ui.controller.rememberCalendarController
import com.nmd.eventCalendar.compose.ui.events.CalendarEventsStore
import com.nmd.eventCalendar.compose.ui.events.PreviewCalendarEventsStore
import com.nmd.eventCalendar.compose.util.isPhoneLandscapeWindow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private val MonthHeaderWidthLandscape = 48.dp

/**
 * Root composable that renders the full calendar UI.
 *
 * This composable adapts its layout based on the current window size:
 * - In **portrait** mode a vertical layout is used with the month header at the top.
 * - In **phone landscape** mode a horizontal layout is used with the month header rendered as a
 *   side bar on the leading edge.
 *
 * Internally, [CalendarScreen] wires together:
 * - [MonthHeader] for month title and previous/next navigation.
 * - [WeekHeader] for the weekday label row (Mon–Sun).
 * - [WeekNumberColumn] for the optional ISO week-number column.
 * - A [HorizontalPager] backed by [CalendarController] for month-to-month swiping.
 * - [MonthView] for the actual 6×7 day grid inside each pager page.
 *
 * Event data is consumed from [calendarEventsStore] via its [CalendarEventsStore.eventsByDateFlow]
 * and resolved per date on each composition.
 *
 * @param modifier Modifier applied to the root layout container.
 * @param calendarController Controller that owns the [PagerState] and provides navigation helpers.
 * @param calendarEventsStore Store that exposes events grouped by date as a [kotlinx.coroutines.flow.StateFlow].
 * @param calendarOptions Configuration options (week start, header/week-number visibility, date bounds).
 * @param calendarStyle Styling configuration (colors, typography sizes).
 * @param onDaySelected Callback invoked when the user taps a day cell.
 * @param onMonthChange Callback invoked whenever the visible month changes (paging or programmatic navigation).
 */
@Composable
internal fun CalendarScreen(
    modifier: Modifier,
    calendarController: CalendarController,
    calendarEventsStore: CalendarEventsStore,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    onDaySelected: (CalendarDay) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    val onMonthChangeState = rememberUpdatedState(onMonthChange)
    val onDaySelectedState = rememberUpdatedState(onDaySelected)

    val pagerState = calendarController.pagerState

    val currentMonth by remember(calendarController, pagerState) {
        derivedStateOf { calendarController.pageToMonth(pagerState.currentPage) }
    }

    LaunchedEffect(pagerState, calendarController) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .map { page -> calendarController.pageToMonth(page) }
            .distinctUntilChanged()
            .collect { month -> onMonthChangeState.value(month) }
    }

    val eventsByDate = calendarEventsStore.eventsByDateFlow.collectAsStateWithLifecycle().value
    val eventsForDate: (LocalDate) -> List<Event> = remember(eventsByDate) {
        { date -> eventsByDate[date].orEmpty() }
    }

    val onPrevMonth: () -> Unit =
        remember(calendarController) { { calendarController.goToPreviousMonth() } }
    val onNextMonth: () -> Unit =
        remember(calendarController) { { calendarController.goToNextMonth() } }

    val phoneLandscape = isPhoneLandscapeWindow()

    if (phoneLandscape) {
        val rootModifier = modifier.calendarRoot(options = calendarOptions, isPhoneLandscape = true)

        Row(modifier = rootModifier) {
            if (calendarOptions.headerVisible) {
                Column(
                    modifier = Modifier
                        .width(MonthHeaderWidthLandscape)
                        .then(if (calendarOptions.isCurrentWeekOnly) Modifier.wrapContentHeight() else Modifier.fillMaxHeight()),
                    verticalArrangement = Arrangement.Top
                ) {
                    MonthHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = onPrevMonth,
                        onNextMonth = onNextMonth,
                        calendarStyle = calendarStyle,
                        layout = MonthHeaderLayout.SideBar,
                        showNavigation = !calendarOptions.isCurrentWeekOnly
                    )
                }
            }

            Column(
                modifier = Modifier
                    .then(
                        if (calendarOptions.isCurrentWeekOnly) Modifier
                            .fillMaxWidth()
                            .wrapContentHeight() else Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    )
            ) {
                CalendarPagerSection(
                    modifier = Modifier.calendarMonthGrid(
                        options = calendarOptions,
                        isPhoneLandscape = true
                    ),
                    pagerState = pagerState,
                    calendarController = calendarController,
                    calendarOptions = calendarOptions,
                    calendarStyle = calendarStyle,
                    currentMonth = currentMonth,
                    eventsForDate = eventsForDate,
                    onDaySelected = { day -> onDaySelectedState.value(day) },
                    phoneLandscape = true
                )
            }
        }
    } else {
        val rootModifier =
            modifier.calendarRoot(options = calendarOptions, isPhoneLandscape = false)

        Column(modifier = rootModifier) {
            if (calendarOptions.headerVisible) {
                MonthHeader(
                    currentMonth = currentMonth,
                    onPreviousMonth = onPrevMonth,
                    onNextMonth = onNextMonth,
                    calendarStyle = calendarStyle,
                    layout = MonthHeaderLayout.TopBar,
                    showNavigation = !calendarOptions.isCurrentWeekOnly
                )
            } else {
                Column(Modifier.height(0.dp)) {}
            }

            CalendarPagerSection(
                modifier = Modifier.calendarMonthGrid(
                    options = calendarOptions,
                    isPhoneLandscape = false
                ),
                pagerState = pagerState,
                calendarController = calendarController,
                calendarOptions = calendarOptions,
                calendarStyle = calendarStyle,
                currentMonth = currentMonth,
                eventsForDate = eventsForDate,
                onDaySelected = { day -> onDaySelectedState.value(day) },
                phoneLandscape = false
            )
        }
    }
}

/**
 * Section that contains the [WeekHeader] and the [MonthPager] (with optional [WeekNumberColumn]).
 *
 * @param modifier Modifier applied to the section.
 * @param pagerState The state of the horizontal pager.
 * @param calendarController Controller for calendar navigation and page-to-month mapping.
 * @param calendarOptions Configuration options for the calendar.
 * @param calendarStyle Styling configuration for the calendar.
 * @param currentMonth The currently visible month.
 * @param eventsForDate Function to retrieve events for a specific date.
 * @param onDaySelected Callback when a day is selected.
 * @param phoneLandscape Whether the device is in phone landscape mode.
 */
@Composable
private fun ColumnScope.CalendarPagerSection(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    calendarController: CalendarController,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    currentMonth: YearMonth,
    eventsForDate: (LocalDate) -> List<Event>,
    onDaySelected: (CalendarDay) -> Unit,
    phoneLandscape: Boolean
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.calendarBody(
            columnScope = this,
            options = calendarOptions,
            isPhoneLandscape = phoneLandscape,
            scrollState = scrollState
        )
    ) {
        WeekHeader(
            currentMonth = currentMonth,
            calendarOptions = calendarOptions,
            calendarStyle = calendarStyle,
            phoneLandscape = phoneLandscape
        )

        Column(
            modifier = Modifier.calendarMonthGrid(
                options = calendarOptions,
                isPhoneLandscape = phoneLandscape
            )
        ) {
            if (calendarOptions.calendarWeekVisible) {
                val monthOptionsNoWeek = remember(calendarOptions) {
                    calendarOptions.copy(calendarWeekVisible = false)
                }

                Row(
                    modifier = Modifier.calendarMonthGrid(
                        options = calendarOptions,
                        isPhoneLandscape = phoneLandscape
                    )
                ) {
                    WeekNumberColumn(
                        modifier = if (phoneLandscape) Modifier.width(CalendarWeekColumnWidth) else Modifier.weight(
                            1f
                        ),
                        yearMonth = currentMonth,
                        weekStart = calendarOptions.weekStart,
                        calendarStyle = calendarStyle,
                        calendarOptions = calendarOptions,
                        phoneLandscape = phoneLandscape
                    )

                    MonthPager(
                        modifier = Modifier
                            .then(
                                if (calendarOptions.isCurrentWeekOnly) Modifier
                                    .wrapContentHeight()
                                    .weight(if (phoneLandscape) 1f else 7f) else Modifier.weight(if (phoneLandscape) 1f else 7f)
                            ),
                        pagerState = pagerState,
                        calendarController = calendarController,
                        calendarOptions = monthOptionsNoWeek,
                        calendarStyle = calendarStyle,
                        eventsForDate = eventsForDate,
                        onDaySelected = onDaySelected,
                        phoneLandscape = phoneLandscape
                    )
                }
            } else {
                MonthPager(
                    modifier = Modifier.calendarMonthGrid(
                        options = calendarOptions,
                        isPhoneLandscape = phoneLandscape
                    ),
                    pagerState = pagerState,
                    calendarController = calendarController,
                    calendarOptions = calendarOptions,
                    calendarStyle = calendarStyle,
                    eventsForDate = eventsForDate,
                    onDaySelected = onDaySelected,
                    phoneLandscape = phoneLandscape
                )
            }
        }
    }
}

/**
 * Horizontal pager that allows swiping between months.
 *
 * @param modifier Modifier applied to the pager.
 * @param pagerState The state of the horizontal pager.
 * @param calendarController Controller for calendar navigation and page-to-month mapping.
 * @param calendarOptions Configuration options for the calendar.
 * @param calendarStyle Styling configuration for the calendar.
 * @param eventsForDate Function to retrieve events for a specific date.
 * @param onDaySelected Callback when a day is selected.
 * @param phoneLandscape Whether the device is in phone landscape mode.
 */
@Composable
private fun MonthPager(
    modifier: Modifier,
    pagerState: PagerState,
    calendarController: CalendarController,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    eventsForDate: (LocalDate) -> List<Event>,
    onDaySelected: (CalendarDay) -> Unit,
    phoneLandscape: Boolean
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSize = PageSize.Fill,
        beyondViewportPageCount = 0,
        key = { page -> calendarController.pageToMonth(page).let { "${it.year}-${it.monthValue}" } }
    ) { page ->
        val month = calendarController.pageToMonth(page)

        MonthView(
            yearMonth = month,
            calendarOptions = calendarOptions,
            calendarStyle = calendarStyle,
            eventsForDate = eventsForDate,
            onDaySelected = onDaySelected,
            phoneLandscape = phoneLandscape
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun CalendarScreenPreview() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
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
        calendarEventsStore = store,
        onDaySelected = {},
        onMonthChange = {},
        calendarOptions = defaultCalendarOptions().copy(calendarWeekVisible = true),
        calendarStyle = defaultCalendarStyle()
    )
}