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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.MonthHeaderLayout
import kotlinx.coroutines.launch
import java.time.YearMonth

@Composable
fun CalendarScreen(
    modifier: Modifier,
    calendarController: CalendarController,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    onDaySelected: (CalendarDay) -> Unit
) {
    val pagerState = calendarController.pagerState
    val scope = calendarController.scope
    val baseMonth = calendarController.baseMonth
    val basePage = calendarController.basePage

    val currentMonth by remember {
        derivedStateOf {
            baseMonth.plusMonths((pagerState.currentPage - basePage).toLong())
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight

        val weekHeaderHeight = 32.dp
        val monthHeaderHeightPortrait = 48.dp
        val monthHeaderWidthLandscape = 48.dp

        if (!isLandscape) {
            val heightAfterMonthHeader =
                maxHeight - (if (calendarOptions.headerVisible) monthHeaderHeightPortrait else 0.dp)
            val gridHeight = (heightAfterMonthHeader - weekHeaderHeight).coerceAtLeast(0.dp)

            Column(Modifier.fillMaxSize()) {
                if (calendarOptions.headerVisible) {
                    MonthHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                        onNextMonth = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
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
                    modifier = Modifier.fillMaxSize(),
                    onDaySelected = onDaySelected
                )
            }
        } else {
            val rightWidth = (maxWidth - monthHeaderWidthLandscape).coerceAtLeast(0.dp)
            val gridHeight = (maxHeight - weekHeaderHeight).coerceAtLeast(0.dp)

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
                            onPreviousMonth = {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage - 1
                                    )
                                }
                            },
                            onNextMonth = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
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
    pagerState: androidx.compose.foundation.pager.PagerState,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    currentMonth: YearMonth,
    weekHeaderHeight: Dp,
    gridHeight: Dp,
    onDaySelected: (CalendarDay) -> Unit
) {
    Column(modifier = modifier) {
        WeekHeader(
            currentMonth = currentMonth,
            itemHeight = weekHeaderHeight,
            calendarOptions = calendarOptions,
            calendarStyle = calendarStyle
        )

        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight),
            state = pagerState,
            pageSize = PageSize.Fill
        ) { page ->
            val month = baseMonth.plusMonths((page - basePage).toLong())
            MonthView(
                yearMonth = month,
                calendarOptions = calendarOptions,
                calendarStyle = calendarStyle,
                onDaySelected = onDaySelected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen(
        modifier = Modifier.fillMaxSize(),
        calendarController = rememberCalendarController(),
        calendarOptions = defaultCalendarOptions(),
        calendarStyle = defaultCalendarStyle(),
        onDaySelected = {}
    )
}