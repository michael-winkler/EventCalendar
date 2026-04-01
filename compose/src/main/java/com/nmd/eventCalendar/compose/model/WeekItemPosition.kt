package com.nmd.eventCalendar.compose.model

/**
 * Indicates the vertical position of an item within a week row.
 *
 * This is typically used for drawing/background shapes or decorations differently for
 * the first, middle, and last rows of a month grid.
 */
enum class WeekItemPosition {

    /** Item is located in the first (top) week row of the grid. */
    Top,

    /** Item is located in a middle week row of the grid. */
    Middle,

    /** Item is located in the last (bottom) week row of the grid. */
    Bottom
}