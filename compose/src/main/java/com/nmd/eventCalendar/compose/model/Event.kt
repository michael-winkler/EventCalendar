package com.nmd.eventCalendar.compose.model

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents a calendar event bound to a specific [date].
 *
 * This model is designed for UI rendering in Compose. It includes display attributes such as
 * [shapeColor] and [textColor], optional metadata via [data], an optional time range via
 * [timeRange], and a stable [id] that can be used as a key in lazy lists.
 *
 * ## Stable identity ([id])
 * A stable, unique identifier is recommended so Compose can use it as a stable key
 * (e.g. in `LazyColumn(items, key = { it.id })`), which improves item stability and animations.
 *
 * - If you already have a stable ID (e.g. a database primary key), pass it via [id].
 * - If you do not pass an ID, a process-local unique ID is generated automatically.
 *
 * Note: The auto-generated ID is unique only within the current app process. It is not intended
 * for persistence across app restarts.
 *
 * @property date The calendar date on which the event occurs.
 * @property name Display name/title of the event.
 * @property shapeColor Background/accent color used to render the event chip/shape.
 * @property textColor Text color used when rendering the event name.
 * @property data Optional user-defined payload associated with the event (e.g. your domain model).
 * @property timeRange Optional start/end time information for sorting and display.
 * @property id Stable unique identifier for this event; used for stable UI keys.
 */
@Keep
data class Event(
    val date: LocalDate,
    val name: String,
    val shapeColor: Color,
    val textColor: Color,
    val data: Any? = null,
    val timeRange: EventTimeRange? = null,
    val id: Int = nextEventId()
) {
    companion object {
        /**
         * Process-local counter used to generate unique IDs when callers do not provide one.
         * This is sufficient for stable Compose keys during the lifetime of the process.
         */
        private val idCounter = AtomicInteger(1)

        /** Returns the next process-local unique event ID. */
        private fun nextEventId(): Int = idCounter.getAndIncrement()
    }
}