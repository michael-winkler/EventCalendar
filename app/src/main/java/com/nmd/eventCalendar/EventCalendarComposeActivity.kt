package com.nmd.eventCalendar

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.nmd.eventCalendar.dtos.Event
import com.nmd.eventCalendar.dtos.EventTimeRange
import kotlinx.datetime.*
import java.time.LocalDate as JavaLocalDate
import java.time.Year
import kotlin.random.Random

val Color.Companion.White: Color
    get() = androidx.compose.ui.graphics.Color.White

/**
 * Generates a list of sample events for the current year.
 *
 * @param templates A list of event templates, each consisting of a name and a color.
 * @param eventCount The desired number of events to generate. Defaults to 250.
 * @param seed A seed for the random number generator to ensure reproducible results. Defaults to the current system time.
 * @return A list of generated Event objects.
 */
fun shuffleEventsForCurrentYear(
    templates: List<Pair<String, Color>>,
    eventCount: Int = 250,
    seed: Long = System.currentTimeMillis()
): List<Event> {
    if (templates.isEmpty() || eventCount <= 0) return emptyList()

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val year = today.year
    val start = LocalDate(year, 1, 1)
    val rnd = Random(seed)

    // Check if it's a leap year
    val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    val daysInYear = if (isLeap) 366 else 365

    return List(eventCount) {
        val (name, shape) = templates[rnd.nextInt(templates.size)]
        // Use daysInYear to generate a random date within the current year
        val date = start.plus(rnd.nextInt(daysInYear), kotlinx.datetime.DateTimeUnit.DAY)

        val hasTime = rnd.nextBoolean()
        val timeRange = if (hasTime) {
            val startHour = rnd.nextInt(8, 20)
            val duration = rnd.nextInt(1, 4)
            EventTimeRange(startHour, 0, startHour + duration, 0)
        } else null

        Event(
            date = date,
            name = name,
            shapeColor = shape,
            textColor = Color.White,
            timeRange = timeRange
        )
    }
}
