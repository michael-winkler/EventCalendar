package com.nmd.eventCalendar.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.nmd.eventCalendar.R
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import java.util.Calendar

class Utils {

    companion object {

        fun Context?.getActivity(): Activity? {
            return when (this) {
                is Activity -> this
                is ContextWrapper -> this.baseContext.getActivity()
                else -> null
            }
        }

        fun Int.getDaysOfMonthAndGivenYear(year: Int): List<Day> {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.MONTH, this@getDaysOfMonthAndGivenYear)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek =
                calendar.get(Calendar.DAY_OF_WEEK) - 2 // Adjust for zero-based indexing
            val numEmptyCells = (firstDayOfWeek + 7) % 7

            val days = mutableListOf<Day>()

            // Previous month
            val prevMonthDays = (1..numEmptyCells).map {
                val prevMonth = (this@getDaysOfMonthAndGivenYear + 11) % 12
                val prevYear = if (prevMonth == 11) year - 1 else year
                val dayOfMonth =
                    prevMonth.getDaysInMonthAndGivenYear(prevYear).last() - numEmptyCells + it
                Day(
                    value = dayOfMonth.toString(),
                    isCurrentMonth = false,
                    isCurrentDay = false,
                    date = String.format("%02d.%02d.%04d", dayOfMonth, prevMonth + 1, prevYear)
                )
            }
            days.addAll(prevMonthDays)

            // Current month
            val today = Calendar.getInstance()
            val currentMonthDays = (1..numDaysInMonth).map { dayOfMonth ->
                val isCurrentDay =
                    (today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == this@getDaysOfMonthAndGivenYear && today.get(
                        Calendar.DAY_OF_MONTH
                    ) == dayOfMonth)
                Day(
                    value = dayOfMonth.toString(),
                    isCurrentMonth = true,
                    isCurrentDay = isCurrentDay,
                    date = String.format(
                        "%02d.%02d.%04d", dayOfMonth, this@getDaysOfMonthAndGivenYear + 1, year
                    )
                )
            }
            days.addAll(currentMonthDays)

            // Next month
            val numRemainingCells = 42 - days.size
            val nextMonthDays = (1..numRemainingCells).map {
                val nextMonth = (this@getDaysOfMonthAndGivenYear + 1) % 12
                val nextYear = if (nextMonth == 0) year + 1 else year
                val dayOfMonth = it
                Day(
                    value = dayOfMonth.toString(),
                    isCurrentMonth = false,
                    isCurrentDay = false,
                    date = String.format("%02d.%02d.%04d", dayOfMonth, nextMonth + 1, nextYear)
                )
            }
            days.addAll(nextMonthDays)

            return days
        }

        private fun Int.getDaysInMonthAndGivenYear(year: Int): List<Int> {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.MONTH, this@getDaysInMonthAndGivenYear)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            return (1..numDaysInMonth).toList()
        }

        fun Int.getMonthName(context: Context): String {
            val array = context.resources.getStringArray(R.array.ecv_month_names)
            return array[this]
        }

        fun Day.dayEvents(events: ArrayList<Event>): ArrayList<Event> {
            return ArrayList(events.filter { it.date == date })
        }

        fun Int.isDarkColor(): Boolean {
            return ColorUtils.calculateLuminance(this) < 0.5
        }

        fun ViewPager2?.disableItemAnimation() {
            val view: RecyclerView? = this?.getChildAt(0) as RecyclerView?
            view?.itemAnimator = null
        }

        fun <T> ArrayList<T>?.orEmptyArrayList(): ArrayList<T> {
            return this ?: ArrayList()
        }

    }
}