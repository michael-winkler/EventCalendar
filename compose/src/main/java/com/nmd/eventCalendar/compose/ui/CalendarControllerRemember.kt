package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import java.time.YearMonth

private const val CALENDAR_BASE_PAGE = Int.MAX_VALUE / 2

@Composable
fun rememberCalendarController(): CalendarController {
    val pagerState = rememberPagerState(
        initialPage = CALENDAR_BASE_PAGE,
        pageCount = { Int.MAX_VALUE }
    )
    val scope = rememberCoroutineScope()
    val baseMonth = remember { YearMonth.now() }

    return remember(pagerState, scope, baseMonth) {
        CalendarController(
            pagerState = pagerState,
            scope = scope,
            basePage = CALENDAR_BASE_PAGE,
            baseMonth = baseMonth
        )
    }
}