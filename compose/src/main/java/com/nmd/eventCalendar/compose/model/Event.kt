package com.nmd.eventCalendar.compose.model

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import com.nmd.eventCalendar.compose.model.serializers.ColorSerializer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.pow

/**
 * Represents a calendar event bound to a specific [date].
 *
 * This model is primarily designed for UI rendering in Jetpack Compose. Besides the core event
 * information, it contains UI-related properties such as [shapeColor] and [textColor], optional
 * metadata via [data], and an optional [timeRange] that can be used for sorting and display.
 *
 * ## Stable identity ([id])
 * A stable, unique identifier is recommended so Compose can use it as a stable key
 * (e.g. in `LazyColumn(items, key = { it.id })`), which improves item stability and animations.
 *
 * - If you already have a stable ID (e.g., a database primary key), pass it via [id].
 * - If you do not pass an ID, a process-local unique ID is generated automatically.
 *
 * Note: The auto-generated ID is unique only within the current app process. It is not intended
 * for persistence across app restarts.
 *
 * ## Automatic text color improvement
 * If [autoAdjustTextColorForBackground] is enabled, the event will expose [effectiveTextColor]
 * which may override [textColor] to improve readability depending on [shapeColor].
 *
 * Current rule:
 * - If [shapeColor] is considered "light" and [textColor] is not considered "dark",
 *   [effectiveTextColor] becomes [Color.Black].
 * - Otherwise, [effectiveTextColor] remains [textColor].
 *
 * ## Multi-day events
 * An event may span more than one day by providing an [endDate]. In that case, [date] is treated as
 * the (inclusive) start day and [endDate] as the (inclusive) end day. Multi-day events are rendered
 * as a single continuous bar across the day cells they cover, correctly split across week boundaries
 * and independent of the configured week start.
 *
 * - If [endDate] is `null`, the event is a single-day event on [date].
 * - If [endDate] is before [date], it is ignored and the event is treated as single-day (see [lastDate]).
 *
 * @property date The calendar date on which the event starts (inclusive).
 * @property name Display name/title of the event.
 * @property shapeColor Background/accent color used to render the event chip/shape.
 * @property textColor Preferred text color used when rendering the event name.
 * @property autoAdjustTextColorForBackground If true, [effectiveTextColor] may override [textColor]
 * to improve contrast based on [shapeColor].
 * @property data Optional user-defined payload associated with the event (e.g., your domain model).
 * @property timeRange Optional start/end time information for sorting and display.
 * @property endDate Optional inclusive end day for multi-day events. If `null` (default) the event
 * occupies only [date]. Values before [date] are ignored (treated as single-day).
 * @property id Stable unique identifier for this event; used for stable UI keys.
 */
@Keep
@Serializable
data class Event(
    val date: LocalDate,
    val name: String,
    @Serializable(with = ColorSerializer::class)
    val shapeColor: Color,
    @Serializable(with = ColorSerializer::class)
    val textColor: Color,
    val autoAdjustTextColorForBackground: Boolean = true,
    @Transient
    val data: Any? = null,
    val timeRange: EventTimeRange? = null,
    val endDate: LocalDate? = null,
    val id: Int = nextEventId()
) {

    /**
     * The last (inclusive) day this event covers.
     *
     * Equals [date] for single-day events. For multi-day events it equals [endDate], unless
     * [endDate] is before [date], in which case [date] is returned (invalid ranges are coerced).
     */
    val lastDate: LocalDate
        get() = endDate?.let { if (it < date) date else it } ?: date

    /**
     * Whether this event spans more than a single day.
     */
    val isMultiDay: Boolean
        get() = lastDate > date

    /**
     * Number of days this event covers, inclusive of both start and end (always >= 1).
     */
    val spanDays: Int
        get() = date.daysUntil(lastDate) + 1

    /**
     * Returns whether this event covers the given [day] (inclusive on both ends).
     */
    fun occursOn(day: LocalDate): Boolean = day >= date && day <= lastDate

    /**
     * The text color that should actually be used by the UI.
     *
     * If [autoAdjustTextColorForBackground] is enabled, this value may differ from [textColor]
     * to improve readability depending on [shapeColor].
     */
    val effectiveTextColor: Color
        get() = if (autoAdjustTextColorForBackground) {
            improveTextColorIfNeeded(shapeColor = shapeColor, currentTextColor = textColor)
        } else {
            textColor
        }

    companion object {
        private val idCounter = AtomicInteger(1)

        /**
         * Returns the next process-local unique event ID.
         *
         * This is sufficient for stable Compose keys during the lifetime of the process.
         */
        private fun nextEventId(): Int = idCounter.getAndIncrement()

        private fun improveTextColorIfNeeded(shapeColor: Color, currentTextColor: Color): Color {
            val shapeIsLight = shapeColor.isLight()
            val textIsDark = currentTextColor.isDark()
            return if (shapeIsLight && !textIsDark) Color.Black else currentTextColor
        }

        private fun Color.isLight(): Boolean = relativeLuminance() >= 0.6f

        private fun Color.isDark(): Boolean = relativeLuminance() <= 0.4f

        /**
         * Computes the relative luminance of this color according to WCAG (sRGB).
         *
         * The result is in the range [0, 1], where 0 is black and 1 is white.
         */
        private fun Color.relativeLuminance(): Float {
            fun linearize(c: Float): Float =
                if (c <= 0.04045f) c / 12.92f else ((c + 0.055f) / 1.055f).pow(2.4f)

            val r = linearize(red)
            val g = linearize(green)
            val b = linearize(blue)
            return (0.2126f * r + 0.7152f * g + 0.0722f * b)
        }
    }
}