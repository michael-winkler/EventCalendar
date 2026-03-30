package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.YearMonth

class CalendarController internal constructor(
    internal val pagerState: PagerState,
    internal val scope: CoroutineScope,
    internal val basePage: Int,
    internal val baseMonth: YearMonth
) {
    fun jumpToCurrentMonth(animated: Boolean = true) {
        scope.launch {
            if (animated) pagerState.animateScrollToPage(basePage)
            else pagerState.scrollToPage(basePage)
        }
    }
}