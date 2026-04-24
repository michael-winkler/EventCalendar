package com.nmd.eventCalendar.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.RippleDrawable
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.nmd.eventCalendar.R
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Clock

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class Utils {

    companion object {

        internal fun Int.getDaysOfMonthAndGivenYear(
            year: Int,
            weekStartDay: Int
        ): List<Day> {
            val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, this@getDaysOfMonthAndGivenYear)
                set(Calendar.DAY_OF_MONTH, 1)
            }

            val numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)

            val offset = (firstDayOfMonth - weekStartDay + 7) % 7

            val days = mutableListOf<Day>()

            // Previous month
            val prevMonth = (this@getDaysOfMonthAndGivenYear + 11) % 12
            val prevYear = if (prevMonth == 11) year - 1 else year
            val prevMonthDays = prevMonth.getDaysInMonthAndGivenYear(prevYear)

            repeat(offset) { index ->
                val dayOfMonth = prevMonthDays.last() - offset + index + 1
                val date = String.format(
                    Locale.GERMAN,
                    "%02d.%02d.%04d",
                    dayOfMonth,
                    prevMonth + 1,
                    prevYear
                )
                days.add(
                    Day(
                        value = dayOfMonth.toString(),
                        isCurrentMonth = false,
                        isCurrentDay = currentDate == date,
                        date = date
                    )
                )
            }

            // Current month
            val today = Calendar.getInstance()
            (1..numDaysInMonth).forEach { dayOfMonth ->
                val isCurrentDay =
                    today.get(Calendar.YEAR) == year &&
                            today.get(Calendar.MONTH) == this@getDaysOfMonthAndGivenYear &&
                            today.get(Calendar.DAY_OF_MONTH) == dayOfMonth

                days.add(
                    Day(
                        value = dayOfMonth.toString(),
                        isCurrentMonth = true,
                        isCurrentDay = isCurrentDay,
                        date = String.format(
                            Locale.GERMAN,
                            "%02d.%02d.%04d",
                            dayOfMonth,
                            this@getDaysOfMonthAndGivenYear + 1,
                            year
                        )
                    )
                )
            }

            // Next month
            val nextMonth = (this@getDaysOfMonthAndGivenYear + 1) % 12
            val nextYear = if (nextMonth == 0) year + 1 else year

            while (days.size < 42) {
                val dayOfMonth = days.size - (offset + numDaysInMonth) + 1
                val date = String.format(
                    Locale.GERMAN,
                    "%02d.%02d.%04d",
                    dayOfMonth,
                    nextMonth + 1,
                    nextYear
                )
                days.add(
                    Day(
                        value = dayOfMonth.toString(),
                        isCurrentMonth = false,
                        isCurrentDay = currentDate == date,
                        date = date
                    )
                )
            }

            return days
        }

        internal fun getDaysForCurrentWeek(weekStartDay: Int): List<Day> {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val desiredStartDay = calendarDayToDayOfWeek(weekStartDay)

            val daysUntilStart = (today.dayOfWeek.ordinal - desiredStartDay.ordinal + 7) % 7
            val actualStartOfWeek = today.minus(daysUntilStart, DateTimeUnit.DAY)

            return List(7) { index ->
                val currentDate = actualStartOfWeek.plus(index, DateTimeUnit.DAY)
                val dateStr = String.format(
                    Locale.GERMAN,
                    "%02d.%02d.%04d",
                    currentDate.day,
                    currentDate.month.number,
                    currentDate.year
                )
                Day(
                    value = currentDate.day.toString(),
                    isCurrentMonth = true,
                    isCurrentDay = today == currentDate,
                    date = dateStr
                )
            }
        }

        private fun calendarDayToDayOfWeek(day: Int): DayOfWeek =
            when (day) {
                Calendar.MONDAY -> DayOfWeek.MONDAY
                Calendar.SUNDAY -> DayOfWeek.SUNDAY
                Calendar.TUESDAY -> DayOfWeek.TUESDAY
                Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
                Calendar.THURSDAY -> DayOfWeek.THURSDAY
                Calendar.FRIDAY -> DayOfWeek.FRIDAY
                Calendar.SATURDAY -> DayOfWeek.SATURDAY
                else -> DayOfWeek.MONDAY
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

        internal fun Int.getMonthName(context: Context?): String {
            context ?: return ""
            val array = context.resources.getStringArray(R.array.ecv_month_names)
            return array[this]
        }

        internal fun Context?.getDayName(
            day: Int,
            weekStartDay: Int
        ): String {
            this ?: return ""

            val safeDayIndex = (day - 1).coerceIn(0, 6)

            val calendarDay =
                ((weekStartDay - Calendar.SUNDAY + safeDayIndex) % 7) + Calendar.SUNDAY

            return resources.getString(calendarDayToStringRes(calendarDay))
        }

        private fun calendarDayToStringRes(day: Int): Int =
            when (day) {
                Calendar.MONDAY -> R.string.ecv_day_name_monday
                Calendar.TUESDAY -> R.string.ecv_day_name_tuesday
                Calendar.WEDNESDAY -> R.string.ecv_day_name_wednesday
                Calendar.THURSDAY -> R.string.ecv_day_name_thursday
                Calendar.FRIDAY -> R.string.ecv_day_name_friday
                Calendar.SATURDAY -> R.string.ecv_day_name_saturday
                Calendar.SUNDAY -> R.string.ecv_day_name_sunday
                else -> R.string.ecv_day_name_monday
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

            background = ContextCompat.getDrawable(context, expressiveBackgroundRes)
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