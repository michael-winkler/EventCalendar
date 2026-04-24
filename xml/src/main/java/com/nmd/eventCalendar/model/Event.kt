package com.nmd.eventCalendar.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents a calendar event for the XML-based [com.nmd.eventCalendar.EventCalendarView].
 *
 * This model uses a string-based [date] representation (default format "dd.MM.yyyy") and
 * hex-based color strings for [backgroundHexColor] to maintain compatibility across older
 * Android versions without requiring Java 8+ API desugaring.
 *
 * @property date The date of the event as a String (e.g., "31.12.2023").
 * @property name The display name or title of the event.
 * @property backgroundHexColor The background color of the event chip as a hex string (e.g., "#FF0000").
 * @property data Optional user-defined payload associated with the event.
 * @property timeRange Optional start/end time information for the event.
 */
@Keep
@Serializable
data class Event(
    val date: String,
    val name: String,
    val backgroundHexColor: String,
    @Transient
    val data: Any? = null,
    val timeRange: EventTimeRange? = null
)