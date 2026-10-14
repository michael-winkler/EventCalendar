package com.nmd.eventCalendar

import android.util.Log

private const val LogTag = "EventCalendarSample"

/** Logs a sample interaction at INFO level, if that level is enabled for the sample's tag. */
internal fun logInfo(message: String) {
    if (Log.isLoggable(LogTag, Log.INFO)) Log.i(LogTag, message)
}
