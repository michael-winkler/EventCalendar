package com.nmd.eventCalendar.compose.ui.controller

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import java.time.YearMonth
import kotlin.math.max

/**
 * Remembers and creates a [CalendarController] backed by a finite month pager range.
 *
 * The pager range is derived from [CalendarOptions.minDate], [CalendarOptions.maxDate], and
 * [CalendarOptions.openEndedWindowMonths]:
 *
 * - If [CalendarOptions.isCurrentWeekOnly] is true, the pager has exactly 1 page (the current month).
 * - If both min and max are provided, the pager covers the exact inclusive range
 *   from `minMonth` to `maxMonth`.
 * - If only min is provided, the pager covers the inclusive range
 *   from `minMonth` to `minMonth + (window - 1)`.
 * - If only max is provided, the pager covers the inclusive range
 *   from `maxMonth - (window - 1)` to `maxMonth`.
 * - If neither is provided, the pager covers a fixed-size inclusive window centered around the
 *   current month.
 *
 * ## Exact open-ended window size
 * For the "neither is provided" (fully open-ended) case, this implementation guarantees that the
 * total number of months in the pager equals exactly [CalendarOptions.openEndedWindowMonths].
 *
 * Example:
 * - `openEndedWindowMonths = 60` results in exactly 60 pages/months being generated.
 *
 * The current month is placed as close to the center of the range as possible:
 * - monthsBefore = (window - 1) / 2
 * - monthsAfter  = (window - 1) - monthsBefore
 *
 * This avoids the common off-by-one issue where using `window/2` for both sides would produce
 * `window + 1` months for even window sizes.
 *
 * ## Initial page
 * The initial page is set to the current month, clamped to the computed pager range.
 *
 * @param calendarOptions Calendar configuration (bounds and open-ended window size).
 * @return A remembered [CalendarController] instance using a finite pager range.
 */
@Composable
fun rememberCalendarController(
    calendarOptions: CalendarOptions
): CalendarController {
    val nowMonth = remember { YearMonth.now() }

    val minMonth =
        if (calendarOptions.isCurrentWeekOnly) null else calendarOptions.minDate?.let(YearMonth::from)
    val maxMonth =
        if (calendarOptions.isCurrentWeekOnly) null else calendarOptions.maxDate?.let(YearMonth::from)

    val window =
        if (calendarOptions.isCurrentWeekOnly) 1 else max(1, calendarOptions.openEndedWindowMonths)

    fun monthsBetween(start: YearMonth, end: YearMonth): Int =
        (end.year - start.year) * 12 + (end.monthValue - start.monthValue)

    val centeredRangeStartEnd = remember(nowMonth, window) {
        val monthsBefore = (window - 1) / 2
        val monthsAfter = (window - 1) - monthsBefore
        val start = nowMonth.minusMonths(monthsBefore.toLong())
        val end = nowMonth.plusMonths(monthsAfter.toLong())
        start to end
    }

    val rangeStart = remember(
        nowMonth,
        minMonth,
        maxMonth,
        window,
        centeredRangeStartEnd,
        calendarOptions.isCurrentWeekOnly
    ) {
        when {
            calendarOptions.isCurrentWeekOnly -> nowMonth
            minMonth != null && maxMonth != null -> minMonth
            minMonth != null -> minMonth
            maxMonth != null -> maxMonth.minusMonths((window - 1).toLong())
            else -> centeredRangeStartEnd.first
        }
    }

    val rangeEnd = remember(
        nowMonth,
        minMonth,
        maxMonth,
        window,
        centeredRangeStartEnd,
        calendarOptions.isCurrentWeekOnly
    ) {
        when {
            calendarOptions.isCurrentWeekOnly -> nowMonth
            minMonth != null && maxMonth != null -> maxMonth
            maxMonth != null -> maxMonth
            minMonth != null -> minMonth.plusMonths((window - 1).toLong())
            else -> centeredRangeStartEnd.second
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