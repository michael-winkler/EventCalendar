package com.nmd.eventCalendar.compose.util

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import com.nmd.eventcalendar.compose.R
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import java.text.SimpleDateFormat
import java.util.GregorianCalendar

// ---------------------------------------------------------------------------------------------
// Platform boundary (Android): the ONLY place in this library that touches Android resources.
//
// Everything else in :compose is written against common Kotlin / Compose APIs. When the module is
// moved to Kotlin Multiplatform, this file becomes the Android `actual` (or is replaced by Compose
// Multiplatform resources); the composable accessors below are the contract the UI depends on.
// ---------------------------------------------------------------------------------------------

/** Localized short name of [day] (e.g. "Mon"), as shown in the week header. */
@Composable
internal fun dayOfWeekShortName(day: DayOfWeek): String = stringResource(day.toStringRes())

/** Localized full name of [month] (e.g. "January"). */
@Composable
internal fun monthDisplayName(month: Month): String = stringResource(month.toStringRes())

/** Localized label of the calendar-week column header (e.g. "KW" / "CW"). */
@Composable
internal fun calendarWeekLabel(): String = stringResource(R.string.calendar_week_label)

/** Localized label of the all-day area of the time grid (e.g. "All day" / "Ganztägig"). */
@Composable
internal fun allDayLabel(): String = stringResource(R.string.all_day_label)

/**
 * Day and month of [date] in the short numeric form of the current locale, e.g. "14.10." (German)
 * or "10/14" (US English).
 */
@Composable
internal fun shortDateLabel(date: LocalDate): String {
    val languageTag = Locale.current.toLanguageTag()
    val format = remember(languageTag) {
        val locale = java.util.Locale.forLanguageTag(languageTag)
        SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "dM"), locale)
    }
    return remember(format, date) {
        format.format(GregorianCalendar(date.year, date.month.number - 1, date.day).time)
    }
}

/** Localized accessibility label of the previous/next month buttons. */
@Composable
internal fun monthNavigationLabel(isPrevious: Boolean): String =
    stringResource(if (isPrevious) R.string.month_previous else R.string.month_next)

/**
 * Returns the Android string resource ID for the short name of this day of the week.
 *
 * Android-only. Inside the library prefer [dayOfWeekShortName], which keeps the UI platform-neutral.
 */
fun DayOfWeek.toStringRes(): Int = when (this) {
    DayOfWeek.MONDAY -> R.string.day_name_monday
    DayOfWeek.TUESDAY -> R.string.day_name_tuesday
    DayOfWeek.WEDNESDAY -> R.string.day_name_wednesday
    DayOfWeek.THURSDAY -> R.string.day_name_thursday
    DayOfWeek.FRIDAY -> R.string.day_name_friday
    DayOfWeek.SATURDAY -> R.string.day_name_saturday
    DayOfWeek.SUNDAY -> R.string.day_name_sunday
}

/**
 * Returns the Android string resource ID for the full name of this month.
 *
 * Android-only. Inside the library prefer [monthDisplayName], which keeps the UI platform-neutral.
 */
fun Month.toStringRes(): Int = when (this) {
    Month.JANUARY -> R.string.month_january
    Month.FEBRUARY -> R.string.month_february
    Month.MARCH -> R.string.month_march
    Month.APRIL -> R.string.month_april
    Month.MAY -> R.string.month_may
    Month.JUNE -> R.string.month_june
    Month.JULY -> R.string.month_july
    Month.AUGUST -> R.string.month_august
    Month.SEPTEMBER -> R.string.month_september
    Month.OCTOBER -> R.string.month_october
    Month.NOVEMBER -> R.string.month_november
    Month.DECEMBER -> R.string.month_december
}
