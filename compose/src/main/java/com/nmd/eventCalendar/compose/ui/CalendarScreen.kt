package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth

@Composable
fun CalendarScreen(
    modifier: Modifier,
    weekStart: DayOfWeek,
    headerVisible: Boolean
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

    Column(modifier = modifier.fillMaxSize()) {

        if (headerVisible) {
            CalendarMonthHeader(
                currentMonth = currentMonth,
                onPreviousMonth = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                },
                onNextMonth = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            )
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            pageSize = PageSize.Fill
        ) { page ->
            val month = baseMonth.plusMonths((page - Int.MAX_VALUE / 2).toLong())
            CalendarMonthView(
                yearMonth = month,
                weekStart = weekStart
            )
        }
    }
}