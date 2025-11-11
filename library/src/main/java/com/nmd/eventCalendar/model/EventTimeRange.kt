package com.nmd.eventCalendar.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Suppress("unused")
@Keep
@Parcelize
/**
 * Represents a specific time range within a single day.
 *
 * Includes start and end times (hours and minutes) and provides
 * helper methods for formatting, comparison, and duration calculation.
 */
data class EventTimeRange(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
) : Parcelable {

    /**
     * Returns the formatted start time as HH:mm.
     */
    fun getFormattedStart(): String = "%02d:%02d".format(startHour, startMinute)

    /**
     * Returns the formatted end time as HH:mm.
     */
    fun getFormattedEnd(): String = "%02d:%02d".format(endHour, endMinute)

    /**
     * Returns the total duration in minutes between start and end time.
     */
    fun getDurationMinutes(): Int = (endHour * 60 + endMinute) - (startHour * 60 + startMinute)

    /**
     * Returns the duration formatted as HH:mm.
     */
    fun getDurationFormatted(): String {
        val total = getDurationMinutes().coerceAtLeast(0)
        val hours = total / 60
        val minutes = total % 60
        return "%02d:%02d".format(hours, minutes)
    }

    /**
     * Checks if the given time (hour and minute) is within this time range.
     */
    fun contains(hour: Int, minute: Int): Boolean {
        val time = hour * 60 + minute
        val start = startHour * 60 + startMinute
        val end = endHour * 60 + endMinute
        return time in start until end
    }

    /**
     * Returns true if this time range overlaps with another time range.
     */
    fun overlapsWith(other: EventTimeRange): Boolean {
        val startA = startHour * 60 + startMinute
        val endA = endHour * 60 + endMinute
        val startB = other.startHour * 60 + other.startMinute
        val endB = other.endHour * 60 + other.endMinute
        return startA < endB && startB < endA
    }

    /**
     * Returns true if the end time is after the start time.
     */
    fun isValid(): Boolean = getDurationMinutes() > 0

    /**
     * Compares this time range to another by start time.
     */
    fun compareTo(other: EventTimeRange): Int {
        val startA = startHour * 60 + startMinute
        val startB = other.startHour * 60 + other.startMinute
        return startA.compareTo(startB)
    }
}