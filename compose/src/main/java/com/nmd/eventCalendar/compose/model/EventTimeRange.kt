package com.nmd.eventCalendar.compose.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Represents a time interval within a single day.
 *
 * The range is defined by a start time ([startHour]:[startMinute]) and an end time
 * ([endHour]:[endMinute]). The type provides helper methods for formatting, duration
 * calculation, containment checks, and overlap detection.
 *
 * Notes:
 * - This class does not enforce validity at construction time. Use [isValid] if you need to
 *   ensure the end time is after the start time.
 * - The logic assumes the time range does not cross midnight. If you need overnight ranges,
 *   you must handle that separately.
 *
 * @property startHour Start hour in 24-hour format (0..23 recommended).
 * @property startMinute Start minute (0..59 recommended).
 * @property endHour End hour in 24-hour format (0..23 recommended).
 * @property endMinute End minute (0..59 recommended).
 */
@Suppress("unused")
@Keep
@Parcelize
data class EventTimeRange(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
) : Parcelable {

    /**
     * Returns the formatted start time as `HH:mm`.
     */
    fun getFormattedStart(): String = "%02d:%02d".format(startHour, startMinute)

    /**
     * Returns the formatted end time as `HH:mm`.
     */
    fun getFormattedEnd(): String = "%02d:%02d".format(endHour, endMinute)

    /**
     * Returns the duration of the range in minutes.
     *
     * If the end time is before the start time, the result will be negative.
     * Use [isValid] or [getDurationFormatted] if you want to guard against that.
     */
    fun getDurationMinutes(): Int =
        (endHour * 60 + endMinute) - (startHour * 60 + startMinute)

    /**
     * Returns the duration formatted as `HH:mm`.
     *
     * Negative durations are coerced to 0 minutes.
     */
    fun getDurationFormatted(): String {
        val total = getDurationMinutes().coerceAtLeast(0)
        val hours = total / 60
        val minutes = total % 60
        return "%02d:%02d".format(hours, minutes)
    }

    /**
     * Returns whether the given time lies within this range.
     *
     * The check uses a half-open interval: start is inclusive, end is exclusive.
     * In other words, `end` itself is not considered contained.
     *
     * @param hour Hour in 24-hour format.
     * @param minute Minute.
     */
    fun contains(hour: Int, minute: Int): Boolean {
        val time = hour * 60 + minute
        val start = startHour * 60 + startMinute
        val end = endHour * 60 + endMinute
        return time in start until end
    }

    /**
     * Returns whether this time range overlaps with [other].
     *
     * The overlap logic treats both ranges as half-open intervals `[start, end)`.
     */
    fun overlapsWith(other: EventTimeRange): Boolean {
        val startA = startHour * 60 + startMinute
        val endA = endHour * 60 + endMinute
        val startB = other.startHour * 60 + other.startMinute
        val endB = other.endHour * 60 + other.endMinute
        return startA < endB && startB < endA
    }

    /**
     * Returns true if the time range has a positive duration (end is after start).
     */
    fun isValid(): Boolean = getDurationMinutes() > 0

    /**
     * Compares this range to [other] by start time.
     *
     * @return A negative value if this range starts earlier, 0 if equal, or a positive value
     * if this range starts later.
     */
    fun compareTo(other: EventTimeRange): Int {
        val startA = startHour * 60 + startMinute
        val startB = other.startHour * 60 + other.startMinute
        return startA.compareTo(startB)
    }
}