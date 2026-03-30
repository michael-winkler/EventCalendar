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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.MonthHeaderLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.YearMonth

@Composable
fun CalendarScreen(
    modifier: Modifier,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle
) {
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    val scope = rememberCoroutineScope()
    val baseMonth = remember { YearMonth.now() }

    val currentMonth by remember {
        derivedStateOf {
            baseMonth.plusMonths((pagerState.currentPage - Int.MAX_VALUE / 2).toLong())
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
                CalendarMonthHeaderSection(
                    visible = calendarOptions.headerVisible,
                    layout = MonthHeaderLayout.TopBar,
                    currentMonth = currentMonth,
                    pagerState = pagerState,
                    scope = scope,
                    calendarStyle = calendarStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(monthHeaderHeightPortrait)
                )

                CalendarPagerSection(
                    baseMonth = baseMonth,
                    pagerState = pagerState,
                    calendarOptions = calendarOptions,
                    calendarStyle = calendarStyle,
                    currentMonth = currentMonth,
                    weekHeaderHeight = weekHeaderHeight,
                    gridHeight = gridHeight,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            val rightWidth = (maxWidth - monthHeaderWidthLandscape).coerceAtLeast(0.dp)
            val gridHeight = (maxHeight - weekHeaderHeight).coerceAtLeast(0.dp)

            Row(Modifier.fillMaxSize()) {
                CalendarMonthHeaderSection(
                    visible = calendarOptions.headerVisible,
                    layout = MonthHeaderLayout.SideBar,
                    currentMonth = currentMonth,
                    pagerState = pagerState,
                    scope = scope,
                    calendarStyle = calendarStyle,
                    modifier = Modifier
                        .width(monthHeaderWidthLandscape)
                        .fillMaxHeight()
                )

                Column(
                    modifier = Modifier
                        .width(rightWidth)
                        .fillMaxHeight()
                ) {
                    CalendarPagerSection(
                        baseMonth = baseMonth,
                        pagerState = pagerState,
                        calendarOptions = calendarOptions,
                        calendarStyle = calendarStyle,
                        currentMonth = currentMonth,
                        weekHeaderHeight = weekHeaderHeight,
                        gridHeight = gridHeight,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarMonthHeaderSection(
    visible: Boolean,
    layout: MonthHeaderLayout,
    currentMonth: YearMonth,
    pagerState: PagerState,
    scope: CoroutineScope,
    calendarStyle: CalendarStyle,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        MonthHeader(
            currentMonth = currentMonth,
            onPreviousMonth = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            onNextMonth = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
            calendarStyle = calendarStyle,
            layout = layout
        )
    }
}

@Composable
private fun CalendarPagerSection(
    modifier: Modifier = Modifier,
    baseMonth: YearMonth,
    pagerState: PagerState,
    calendarOptions: CalendarOptions,
    calendarStyle: CalendarStyle,
    currentMonth: YearMonth,
    weekHeaderHeight: Dp,
    gridHeight: Dp
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
            val month = baseMonth.plusMonths((page - Int.MAX_VALUE / 2).toLong())
            MonthView(
                yearMonth = month,
                calendarOptions = calendarOptions,
                calendarStyle = calendarStyle
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen(
        modifier = Modifier.fillMaxSize(),
        calendarOptions = defaultCalendarOptions(),
        calendarStyle = defaultCalendarStyle()
    )
}

@Preview(
    name = "CalendarScreen - Landscape",
    showBackground = true,
    widthDp = 932,
    heightDp = 430
)
@Composable
fun CalendarScreenLandscapePreview() {
    CalendarScreen(
        modifier = Modifier.fillMaxSize(),
        calendarOptions = defaultCalendarOptions(),
        calendarStyle = defaultCalendarStyle()
    )
}