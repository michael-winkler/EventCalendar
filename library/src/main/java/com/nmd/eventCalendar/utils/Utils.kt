package com.nmd.eventCalendar.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
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

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class Utils {

    companion object {

        internal fun Context?.getRealContext(): Context? {
            return when (this) {
                is ContextWrapper -> this.baseContext
                is Context -> this
                else -> null
            }
        }

        internal fun Context?.getActivity(): Activity? {
            return when (this) {
                is Activity -> this
                is ContextWrapper -> this.baseContext.getActivity()
                else -> null
            }
        }

        internal fun Int.getDaysOfMonthAndGivenYear(year: Int): List<Day> {
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
                val date = String.format(
                    Locale.GERMAN,
                    "%02d.%02d.%04d",
                    dayOfMonth,
                    prevMonth + 1,
                    prevYear
                )
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
                        Locale.GERMAN,
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
                val date = String.format(
                    Locale.GERMAN,
                    "%02d.%02d.%04d",
                    dayOfMonth,
                    nextMonth + 1,
                    nextYear
                )
                val isCurrentDay = currentDate == date
                Day(
                    value = dayOfMonth.toString(),
                    isCurrentMonth = false,
                    isCurrentDay = isCurrentDay,
                    date = String.format(
                        Locale.GERMAN,
                        "%02d.%02d.%04d",
                        dayOfMonth,
                        nextMonth + 1,
                        nextYear
                    )
                )
            }
            days.addAll(nextMonthDays)
            return days
        }

        internal fun getDaysForCurrentWeek(): List<Day> {
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

            repeat(7) {
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
        internal fun getDaysForCurrentWeekApi26Impl(): List<Day> {
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

        internal fun getCurrentWeekNumber(): Int {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.WEEK_OF_YEAR]
        }

        internal fun getCurrentYear(): Int {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.YEAR]
        }

        internal fun getCurrentMonth(): Int {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.MONTH]
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

        internal fun Int.getMonthName(context: Context?): String {
            context ?: return ""
            val array = context.resources.getStringArray(R.array.ecv_month_names)
            return array[this]
        }

        internal fun Day.dayEvents(events: ArrayList<Event>): ArrayList<Event> {
            return ArrayList(events.filter { it.date == date })
        }

        internal fun Int.isDarkColor(): Boolean {
            return ColorUtils.calculateLuminance(this) < 0.5
        }

        internal fun <T> ArrayList<T>?.orEmptyArrayList(): ArrayList<T> {
            return this ?: ArrayList()
        }

        internal fun Boolean?.orTrue(): Boolean = this ?: true

        internal fun RecyclerView.smoothScrollTo(position: Int) {
            post {
                try {
                    layoutManager?.smoothScrollToPosition(
                        this, RecyclerView.State(), position
                    )
                } catch (_: Exception) {
                    // Nothing to do
                }
            }
        }

        private fun String?.isStringNullOrEmpty(): Boolean {
            return this == null || this == "null" || this.trim().isEmpty()
        }

        internal fun Day.convertStringToCalendarWeek(): String {
            if (date.isStringNullOrEmpty()) {
                return ""
            }
            return try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
                val calendar = Calendar.getInstance(Locale.GERMAN)
                dateFormat.parse(date)?.let {
                    calendar.time = it
                    calendar.get(Calendar.WEEK_OF_YEAR).toString()
                } ?: ""
            } catch (_: Exception) {
                ""
            }
        }

        internal fun Boolean.expressiveCwHelper(
            frameLayout: FrameLayout,
            index: Int,
            cwBackgroundTintColor: Int
        ): Unit = with(frameLayout) {
            val realContext = context.getRealContext() ?: return

            if (!this@expressiveCwHelper) {
                background = null
                return
            }

            val expressiveBackgroundRes = when (index) {
                -1 -> {
                    // Full - Used for ECV Single week view
                    R.drawable.ecv_expressive_full_background
                }

                0 -> {
                    // Top
                    R.drawable.ecv_expressive_top_background
                }

                5 -> {
                    // Bottom
                    R.drawable.ecv_expressive_bottom_background

                }

                else -> {
                    // Inside
                    R.drawable.ecv_expressive_inside_background
                }
            }

            background = ContextCompat.getDrawable(realContext, expressiveBackgroundRes)

            ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(cwBackgroundTintColor))
        }

        internal fun Int.getDimensInt(resources: Resources?): Int {
            return resources?.getDimensionPixelSize(this) ?: 0
        }

        internal fun RippleDrawable.setItemTint(color: Int) {
            val background = findDrawableByLayerId(android.R.id.background)
            background?.setTint(color)
        }

        internal fun Day.getTextTypeface(): Int {
            return if (isCurrentMonth || isCurrentDay) Typeface.BOLD else Typeface.ITALIC
        }

    }
}