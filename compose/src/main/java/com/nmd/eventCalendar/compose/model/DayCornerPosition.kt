package com.nmd.eventCalendar.compose.model

/**
 * Identifies which corner of a day cell is being referenced (e.g. for rounding or drawing
 * decorations).
 *
 * Implementations typically use these values to decide where to apply a corner radius or
 * a marker shape.
 */
enum class DayCornerPosition {
    /** Top-left corner of the day cell. */
    TopLeft,

    /** Top-right corner of the day cell. */
    TopRight,

    /** Bottom-left corner of the day cell. */
    BottomLeft,

    /** Bottom-right corner of the day cell. */
    BottomRight,

    /** Default/fallback corner position. */
    Default
}