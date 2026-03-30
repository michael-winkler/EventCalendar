package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        val monthHeaderHeight = 56.dp
        val weekHeaderHeight = 32.dp

        val heightAfterMonthHeader =
            maxHeight - (if (calendarOptions.headerVisible) monthHeaderHeight else 0.dp)

        val gridHeight = (heightAfterMonthHeader - weekHeaderHeight).coerceAtLeast(0.dp)

        Column(Modifier.fillMaxSize()) {
            if (calendarOptions.headerVisible) {
                CalendarMonthHeader(
                    currentMonth = currentMonth,
                    onPreviousMonth = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    },
                    onNextMonth = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    calendarStyle = calendarStyle
                )
            }

            CalendarWeekHeader(
                currentMonth = currentMonth,
                itemHeight = weekHeaderHeight,
                calendarOptions = calendarOptions,
                calendarStyle = calendarStyle
            )

            HorizontalPager(
                modifier = Modifier.height(gridHeight),
                state = pagerState,
                pageSize = PageSize.Fill
            ) { page ->
                val month = baseMonth.plusMonths((page - Int.MAX_VALUE / 2).toLong())
                CalendarMonthView(
                    yearMonth = month,
                    calendarOptions = calendarOptions,
                    calendarStyle = calendarStyle
                )
            }
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