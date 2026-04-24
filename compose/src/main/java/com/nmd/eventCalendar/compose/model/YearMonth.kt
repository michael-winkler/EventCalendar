package com.nmd.eventCalendar.compose.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * A simple, KMP-friendly representation of a Year and a Month.
 *
 * This class is used internally by the calendar to represent a specific month of a year without
 * requiring a day-of-month component. It provides basic arithmetic for navigating between months
 * and is [Comparable] based on chronological order.
 *
 * @property year The year component.
 * @property month The [Month] component from `kotlinx.datetime`.
 */
data class YearMonth(
    val year: Int,
    val month: Month
) : Comparable<YearMonth> {

    /**
     * Returns the 1-based ISO month number (1 for January, 12 for December).
     */
    val monthValue: Int get() = month.number

    /**
     * Returns a [YearMonth] with the specified number of months added.
     *
     * @param months The number of months to add (can be negative).
     * @return A new [YearMonth] instance representing the result.
     */
    fun plusMonths(months: Long): YearMonth {
        val totalMonths = year * 12 + (monthValue - 1) + months
        val newYear = (totalMonths / 12).toInt()
        val newMonthValue = (totalMonths % 12).toInt() + 1
        return YearMonth(newYear, Month(newMonthValue))
    }

    /**
     * Returns a [YearMonth] with the specified number of months subtracted.
     *
     * @param months The number of months to subtract.
     * @return A new [YearMonth] instance representing the result.
     */
    fun minusMonths(months: Long): YearMonth = plusMonths(-months)

    override fun compareTo(other: YearMonth): Int {
        val yearDiff = year.compareTo(other.year)
        return if (yearDiff != 0) yearDiff else month.compareTo(other.month)
    }

    /**
     * Returns a [LocalDate] for the first day of this month.
     *
     * @param day The day of the month.
     * @return A [LocalDate] instance.
     */
    fun atDay(day: Int): LocalDate = LocalDate(year, month, day)

    companion object {
        /**
         * Returns a [YearMonth] representing the current system year and month.
         */
        fun now(): YearMonth {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return YearMonth(today.year, today.month)
        }

        /**
         * Creates a [YearMonth] from a [LocalDate].
         *
         * @param date The date to extract year and month from.
         * @return A [YearMonth] instance.
         */
        fun from(date: LocalDate): YearMonth = YearMonth(date.year, date.month)
    }
}
