package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.YearMonth
import kotlin.math.max

/**
 * Controller for a month-based pager calendar.
 *
 * The controller provides:
 * - Mapping between pager pages and {@link YearMonth} instances.
 * - Navigation helpers (previous/next/current month, jump to a specific month).
 * - Optional clamping to a min/max month.
 *
 * Important:
 * This controller assumes that the pager provides a FINITE range (finite {@code pageCount}),
 * so it can clamp target pages to {@code [0, pageCount - 1]} and use
 * {@link #canGoToPreviousMonth()} / {@link #canGoToNextMonth()} safely.
 *
 * @param pagerState The pager state used by the underlying HorizontalPager.
 * @param scope Coroutine scope used to run scroll animations.
 * @param basePage The page index that corresponds to {@code baseMonth}.
 *                 Common setups:
 *                 - finite range: {@code basePage = 0} and {@code baseMonth = rangeStart}
 *                 - (pseudo-)infinite range: {@code basePage = Int.MAX_VALUE/2} and {@code baseMonth = YearMonth.now()}
 * @param baseMonth The month that corresponds to {@code basePage}.
 * @param minMonth Optional minimum allowed month (inclusive).
 * @param maxMonth Optional maximum allowed month (inclusive).
 */
class CalendarController internal constructor(
    internal val pagerState: PagerState,
    internal val scope: CoroutineScope,
    internal val basePage: Int,
    internal val baseMonth: YearMonth,
    internal val minMonth: YearMonth?,
    internal val maxMonth: YearMonth?
) {

    /**
     * Converts a pager page index into the corresponding {@link YearMonth}.
     *
     * Formula:
     * {@code month = baseMonth + (page - basePage) months}
     */
    fun pageToMonth(page: Int): YearMonth {
        return baseMonth.plusMonths((page - basePage).toLong())
    }

    /**
     * Converts a {@link YearMonth} into the corresponding pager page index.
     *
     * Formula:
     * {@code page = basePage + monthsBetween(baseMonth, month)}
     */
    fun monthToPage(month: YearMonth): Int {
        val offset = (month.year - baseMonth.year) * 12 + (month.monthValue - baseMonth.monthValue)
        return basePage + offset
    }

    /**
     * Clamps the given month to the configured bounds (if any).
     */
    private fun clampMonth(month: YearMonth): YearMonth {
        var m = month
        if (minMonth != null && m < minMonth) m = minMonth
        if (maxMonth != null && m > maxMonth) m = maxMonth
        return m
    }

    /**
     * Clamps the given page index to the pager's valid range: [0, pageCount - 1].
     */
    private fun clampPage(page: Int): Int {
        val last = max(0, pagerState.pageCount - 1)
        return page.coerceIn(0, last)
    }

    /**
     * Scrolls to the current month (today), clamped to min/max bounds if present.
     *
     * @param animated Whether to animate the scroll.
     */
    fun jumpToCurrentMonth(animated: Boolean = true) {
        goToMonth(YearMonth.now(), animated)
    }

    /**
     * Scrolls to the requested month, clamped to min/max bounds if present.
     *
     * @param month Target month.
     * @param animated Whether to animate the scroll.
     */
    fun goToMonth(month: YearMonth, animated: Boolean = true) {
        val safe = clampMonth(month)
        val page = clampPage(monthToPage(safe))

        scope.launch {
            if (animated) pagerState.animateScrollToPage(page)
            else pagerState.scrollToPage(page)
        }
    }

    /**
     * @return true if the pager is not at the first page.
     */
    fun canGoToPreviousMonth(): Boolean = pagerState.currentPage > 0

    /**
     * @return true if the pager is not at the last page.
     */
    fun canGoToNextMonth(): Boolean = pagerState.currentPage < pagerState.pageCount - 1

    /**
     * Navigates to the previous month, if possible.
     *
     * @param animated Whether to animate the scroll.
     */
    fun goToPreviousMonth(animated: Boolean = true) {
        if (!canGoToPreviousMonth()) return

        val target = pagerState.currentPage - 1
        scope.launch {
            if (animated) pagerState.animateScrollToPage(target)
            else pagerState.scrollToPage(target)
        }
    }

    /**
     * Navigates to the next month, if possible.
     *
     * @param animated Whether to animate the scroll.
     */
    fun goToNextMonth(animated: Boolean = true) {
        if (!canGoToNextMonth()) return

        val target = pagerState.currentPage + 1
        scope.launch {
            if (animated) pagerState.animateScrollToPage(target)
            else pagerState.scrollToPage(target)
        }
    }
}