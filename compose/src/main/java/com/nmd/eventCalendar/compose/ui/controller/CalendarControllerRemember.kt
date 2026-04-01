package com.nmd.eventCalendar.compose.ui.controller

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import java.time.YearMonth
import kotlin.math.max

/**
 * Remembers and creates a [CalendarController] backed by a finite pager range.
 *
 * The pager range is derived from [CalendarOptions.minDate], [CalendarOptions.maxDate], and
 * [CalendarOptions.openEndedWindowMonths]:
 *
 * - If both min and max are provided, the pager covers the exact inclusive range [minMonth and maxMonth].
 * - If only min is provided, the pager covers [minMonth and (minMonth + window - 1)].
 * - If only max is provided, the pager covers [(maxMonth - window + 1) and maxMonth].
 * - If neither is provided, the pager covers a window centered around the current month.
 *
 * The initial page is set to the current month, clamped to the computed range.
 *
 * @param calendarOptions Calendar configuration (bounds and open-ended window size).
 */
@Composable
fun rememberCalendarController(
    calendarOptions: CalendarOptions
): CalendarController {
    val nowMonth = remember { YearMonth.now() }
    val minMonth = calendarOptions.minDate?.let(YearMonth::from)
    val maxMonth = calendarOptions.maxDate?.let(YearMonth::from)
    val window = max(1, calendarOptions.openEndedWindowMonths)

    fun monthsBetween(start: YearMonth, end: YearMonth): Int =
        (end.year - start.year) * 12 + (end.monthValue - start.monthValue)

    val rangeStart = remember(nowMonth, minMonth, maxMonth, window) {
        when {
            minMonth != null && maxMonth != null -> minMonth
            minMonth != null -> minMonth
            maxMonth != null -> maxMonth.minusMonths((window - 1).toLong())
            else -> nowMonth.minusMonths((window / 2).toLong())
        }
    }

    val rangeEnd = remember(nowMonth, minMonth, maxMonth, window) {
        when {
            minMonth != null && maxMonth != null -> maxMonth
            maxMonth != null -> maxMonth
            minMonth != null -> minMonth.plusMonths((window - 1).toLong())
            else -> nowMonth.plusMonths((window / 2).toLong())
        }
    }

    val pageCount = remember(rangeStart, rangeEnd) {
        max(1, monthsBetween(rangeStart, rangeEnd) + 1)
    }

    val startMonth = remember(nowMonth, rangeStart, rangeEnd) {
        when {
            nowMonth < rangeStart -> rangeStart
            nowMonth > rangeEnd -> rangeEnd
            else -> nowMonth
        }
    }

    val initialPage = remember(rangeStart, startMonth, pageCount) {
        monthsBetween(rangeStart, startMonth).coerceIn(0, pageCount - 1)
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pageCount }
    )

    val scope = rememberCoroutineScope()

    return remember(pagerState, scope, rangeStart, minMonth, maxMonth) {
        CalendarController(
            pagerState = pagerState,
            scope = scope,
            basePage = 0,
            baseMonth = rangeStart,
            minMonth = minMonth,
            maxMonth = maxMonth
        )
    }
}