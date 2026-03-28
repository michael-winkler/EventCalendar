package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.DayOfWeek
import java.time.YearMonth

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    weekStart: DayOfWeek = DayOfWeek.MONDAY
) {
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        initialPageOffsetFraction = 0f,
        pageCount = { Int.MAX_VALUE }
    )

    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        state = pagerState,
        pageSize = PageSize.Fill
    ) { page ->

        val baseMonth = YearMonth.now()
        val currentMonth = baseMonth.plusMonths((page - Int.MAX_VALUE / 2).toLong())

        CalendarMonthView(
            yearMonth = currentMonth,
            weekStart = weekStart
        )
    }
}