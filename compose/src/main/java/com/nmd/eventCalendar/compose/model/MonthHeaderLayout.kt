package com.nmd.eventCalendar.compose.model

/**
 * Defines how the month navigation header is laid out in the calendar UI.
 */
internal enum class MonthHeaderLayout {

    /** A horizontal header shown at the top of the calendar (portrait-friendly). */
    TopBar,

    /** A vertical header shown at the side of the calendar (landscape-friendly). */
    SideBar
}