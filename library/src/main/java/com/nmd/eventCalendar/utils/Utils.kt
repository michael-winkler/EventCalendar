package com.nmd.eventCalendar.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.nmd.eventCalendar.R
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Utils {

    companion object {

        fun Context?.getRealContext(): Context? {
            return when (this) {
                is ContextWrapper -> this.baseContext
                is Context -> this
                else -> null
            }
        }

        fun Context?.getActivity(): Activity? {
            return when (this) {
                is Activity -> this
                is ContextWrapper -> this.baseContext.getActivity()
                else -> null
            }
        }

        fun Int.getDaysOfMonthAndGivenYear(year: Int): List<Day> {
            val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

            val calendar = Calendar.getInstance().apply {
                set(Calendar.MONTH, this@getDaysOfMonthAndGivenYear)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val numEmptyCells = (firstDayOfWeek + 7) % 7

            val days = mutableListOf<Day>()

            // Previous month
            val prevMonth = (this@getDaysOfMonthAndGivenYear + 11) % 12
            val prevYear = if (prevMonth == 11) year - 1 else year
            val prevMonthDays = (1..numEmptyCells).map {
                val dayOfMonth =
                    prevMonth.getDaysInMonthAndGivenYear(prevYear).last() - numEmptyCells + it
                val date = String.format("%02d.%02d.%04d", dayOfMonth, prevMonth + 1, prevYear)
                val isCurrentDay = currentDate == date
                Day(
                    value = dayOfMonth.toString(),
                    isCurrentMonth = false,
                    isCurrentDay = isCurrentDay,
                    date = date
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
            val nextMonth = (this@getDaysOfMonthAndGivenYear + 1) % 12
            val nextYear = if (nextMonth == 0) year + 1 else year
            val numRemainingCells = 42 - days.size
            val nextMonthDays = (1..numRemainingCells).map {
                val dayOfMonth = it
                val date = String.format("%02d.%02d.%04d", dayOfMonth, nextMonth + 1, nextYear)
                val isCurrentDay = currentDate == date
                Day(
                    value = dayOfMonth.toString(),
                    isCurrentMonth = false,
                    isCurrentDay = isCurrentDay,
                    date = String.format("%02d.%02d.%04d", dayOfMonth, nextMonth + 1, nextYear)
                )
            }
            days.addAll(nextMonthDays)
            return days
        }

        fun getDaysForCurrentWeek(): List<Day> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return getDaysForCurrentWeekApi26Impl()
            }

            val calendar = Calendar.getInstance()
            val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            val currentYear = calendar.get(Calendar.YEAR)

            val days = mutableListOf<Day>()

            calendar.set(Calendar.WEEK_OF_YEAR, currentWeek)
            calendar.set(Calendar.YEAR, currentYear)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

            for (i in 0 until 7) {
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val date = dateFormat.format(calendar.time)

                days.add(
                    Day(
                        value = dayOfMonth.toString(),
                        isCurrentMonth = true,
                        isCurrentDay = currentDate == date,
                        date = date
                    )
                )

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            return days
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getDaysForCurrentWeekApi26Impl(): List<Day> {
            val today = LocalDate.now()
            val startOfWeek = today.with(DayOfWeek.MONDAY)

            val days = mutableListOf<Day>()

            val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())

            for (i in 0 until 7) {
                val currentDate = startOfWeek.plusDays(i.toLong())
                val dayOfMonth = currentDate.dayOfMonth
                val date = dateFormat.format(currentDate)

                days.add(
                    Day(
                        value = dayOfMonth.toString(),
                        isCurrentMonth = true,
                        isCurrentDay = today == currentDate,
                        date = date
                    )
                )
            }

            return days
        }

        fun getCurrentWeekNumber(): Int {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.WEEK_OF_YEAR]
        }

        fun getCurrentYear(): Int {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.YEAR]
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

        fun Int.getMonthName(context: Context?): String {
            context ?: return ""
            val array = context.resources.getStringArray(R.array.ecv_month_names)
            return array[this]
        }

        fun Day.dayEvents(events: ArrayList<Event>): ArrayList<Event> {
            return ArrayList(events.filter { it.date == date })
        }

        fun Int.isDarkColor(): Boolean {
            return ColorUtils.calculateLuminance(this) < 0.5
        }

        fun <T> ArrayList<T>?.orEmptyArrayList(): ArrayList<T> {
            return this ?: ArrayList()
        }

        fun Boolean?.orTrue(): Boolean = this ?: true

        fun RecyclerView.smoothScrollTo(position: Int) {
            post {
                try {
                    layoutManager?.smoothScrollToPosition(
                        this, RecyclerView.State(), position
                    )
                } catch (ignored: Exception) {
                }
            }
        }

    }
}