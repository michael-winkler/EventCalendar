package com.nmd.eventCalendar.compose.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.datetime.todayIn

/**
 * A simple, KMP-friendly representation of a Year and a Month.
 */
data class YearMonth(
    val year: Int,
    val month: Month
) : Comparable<YearMonth> {

    val monthValue: Int get() = month.number

    fun plusMonths(months: Long): YearMonth {
        val totalMonths = year * 12 + (monthValue - 1) + months
        val newYear = (totalMonths / 12).toInt()
        val newMonthValue = (totalMonths % 12).toInt() + 1
        return YearMonth(newYear, Month(newMonthValue))
    }

    fun minusMonths(months: Long): YearMonth = plusMonths(-months)

    override fun compareTo(other: YearMonth): Int {
        val yearDiff = year.compareTo(other.year)
        return if (yearDiff != 0) yearDiff else month.compareTo(other.month)
    }

    fun atDay(day: Int): LocalDate = LocalDate(year, month, day)

    companion object {
        fun now(): YearMonth {
            val today =
                kotlin.time.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
            return YearMonth(today.year, today.month)
        }

        fun from(date: LocalDate): YearMonth = YearMonth(date.year, date.month)
    }
}
