package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.DayCornerPosition

/**
 * Holds prebuilt day cell corner shapes to avoid re-allocating shapes on every recomposition.
 */
data class DayCornerShapes(
    val outerRadius: Dp = 16.dp,
    val innerRadius: Dp = 4.dp,
    val topLeft: RoundedCornerShape = RoundedCornerShape(
        outerRadius,
        innerRadius,
        innerRadius,
        innerRadius
    ),
    val topRight: RoundedCornerShape = RoundedCornerShape(
        innerRadius,
        outerRadius,
        innerRadius,
        innerRadius
    ),
    val bottomLeft: RoundedCornerShape = RoundedCornerShape(
        innerRadius,
        innerRadius,
        innerRadius,
        outerRadius
    ),
    val bottomRight: RoundedCornerShape = RoundedCornerShape(
        innerRadius,
        innerRadius,
        outerRadius,
        innerRadius
    ),
    val default: RoundedCornerShape = RoundedCornerShape(innerRadius)
) {
    fun forPosition(pos: DayCornerPosition): RoundedCornerShape = when (pos) {
        DayCornerPosition.TopLeft -> topLeft
        DayCornerPosition.TopRight -> topRight
        DayCornerPosition.BottomLeft -> bottomLeft
        DayCornerPosition.BottomRight -> bottomRight
        DayCornerPosition.Default -> default
    }
}

/**
 * Memoizes [DayCornerShapes] for the given radii.
 * Use this to avoid creating new [RoundedCornerShape] instances on recompositions.
 */
@Composable
fun rememberDayCornerShapes(
    outerRadius: Dp = 16.dp,
    innerRadius: Dp = 4.dp
): DayCornerShapes {
    return remember(outerRadius, innerRadius) {
        DayCornerShapes(outerRadius = outerRadius, innerRadius = innerRadius)
    }
}