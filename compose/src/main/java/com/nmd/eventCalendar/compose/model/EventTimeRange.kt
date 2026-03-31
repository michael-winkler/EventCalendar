package com.nmd.eventCalendar.compose.model

import androidx.annotation.Keep

@Suppress("unused")
@Keep
data class EventTimeRange(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
) {
    fun getFormattedStart(): String = "%02d:%02d".format(startHour, startMinute)
    fun getFormattedEnd(): String = "%02d:%02d".format(endHour, endMinute)

    fun getDurationMinutes(): Int =
        (endHour * 60 + endMinute) - (startHour * 60 + startMinute)

    fun getDurationFormatted(): String {
        val total = getDurationMinutes().coerceAtLeast(0)
        val hours = total / 60
        val minutes = total % 60
        return "%02d:%02d".format(hours, minutes)
    }

    fun contains(hour: Int, minute: Int): Boolean {
        val time = hour * 60 + minute
        val start = startHour * 60 + startMinute
        val end = endHour * 60 + endMinute
        return time in start until end
    }

    fun overlapsWith(other: EventTimeRange): Boolean {
        val startA = startHour * 60 + startMinute
        val endA = endHour * 60 + endMinute
        val startB = other.startHour * 60 + other.startMinute
        val endB = other.endHour * 60 + other.endMinute
        return startA < endB && startB < endA
    }

    fun isValid(): Boolean = getDurationMinutes() > 0

    fun compareTo(other: EventTimeRange): Int {
        val startA = startHour * 60 + startMinute
        val startB = other.startHour * 60 + other.startMinute
        return startA.compareTo(startB)
    }
}