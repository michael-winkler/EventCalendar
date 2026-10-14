package com.nmd.eventCalendar.compose.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Icons used by the calendar, defined in code so they work on every Compose platform (no Android
 * drawable resources needed). The paths match the previous `chevron_left` / `chevron_right`
 * vector drawables; the color is applied as a tint by the caller.
 */
internal object CalendarIcons {

    /** Chevron pointing left ("previous month"). */
    val ChevronLeft: ImageVector by lazy {
        chevron("ChevronLeft", "M15.41,16.58L10.83,12L15.41,7.41L14,6L8,12L14,18L15.41,16.58Z")
    }

    /** Chevron pointing right ("next month"). */
    val ChevronRight: ImageVector by lazy {
        chevron("ChevronRight", "M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z")
    }

    private fun chevron(name: String, pathData: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = false
        ).addPath(
            pathData = addPathNodes(pathData),
            fill = SolidColor(Color.Black)
        ).build()
}
