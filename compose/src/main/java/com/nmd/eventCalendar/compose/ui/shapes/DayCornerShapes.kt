package com.nmd.eventCalendar.compose.ui.shapes

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.DayCornerPosition

/**
 * Prebuilt shapes for day cells to avoid allocating new shapes on every recomposition.
 */
@Stable
data class DayCornerShapes(
    val outerRadius: Dp = 16.dp,
    val innerRadius: Dp = 4.dp,
    val topLeft: RoundedCornerShape = RoundedCornerShape(
        topStart = CornerSize(outerRadius),
        topEnd = CornerSize(innerRadius),
        bottomStart = CornerSize(innerRadius),
        bottomEnd = CornerSize(innerRadius)
    ),
    val topRight: RoundedCornerShape = RoundedCornerShape(
        topStart = CornerSize(innerRadius),
        topEnd = CornerSize(outerRadius),
        bottomStart = CornerSize(innerRadius),
        bottomEnd = CornerSize(innerRadius)
    ),
    val bottomLeft: RoundedCornerShape = RoundedCornerShape(
        topStart = CornerSize(innerRadius),
        topEnd = CornerSize(innerRadius),
        bottomStart = CornerSize(outerRadius),
        bottomEnd = CornerSize(innerRadius)
    ),
    val bottomRight: RoundedCornerShape = RoundedCornerShape(
        topStart = CornerSize(innerRadius),
        topEnd = CornerSize(innerRadius),
        bottomStart = CornerSize(innerRadius),
        bottomEnd = CornerSize(outerRadius)
    ),
    val default: RoundedCornerShape = RoundedCornerShape(innerRadius),
    // Special shapes for single week mode
    val firstDayOfWeek: RoundedCornerShape = RoundedCornerShape(
        topStart = CornerSize(outerRadius),
        bottomEnd = CornerSize(innerRadius),
        topEnd = CornerSize(innerRadius),
        bottomStart = CornerSize(outerRadius)
    ),
    val lastDayOfWeek: RoundedCornerShape = RoundedCornerShape(
        topEnd = CornerSize(outerRadius),
        bottomStart = CornerSize(innerRadius),
        topStart = CornerSize(innerRadius),
        bottomEnd = CornerSize(outerRadius)
    )
) {
    fun forPosition(
        position: DayCornerPosition,
        isFirstInSingleWeek: Boolean = false,
        isLastInSingleWeek: Boolean = false
    ): RoundedCornerShape {
        return when {
            isFirstInSingleWeek -> firstDayOfWeek
            isLastInSingleWeek -> lastDayOfWeek
            else -> when (position) {
                DayCornerPosition.TopLeft -> topLeft
                DayCornerPosition.TopRight -> topRight
                DayCornerPosition.BottomLeft -> bottomLeft
                DayCornerPosition.BottomRight -> bottomRight
                DayCornerPosition.Default -> default
            }
        }
    }
}

@Composable
fun rememberDayCornerShapes(
    outerRadius: Dp = 16.dp,
    innerRadius: Dp = 4.dp
): DayCornerShapes {
    return remember(outerRadius, innerRadius) {
        DayCornerShapes(outerRadius = outerRadius, innerRadius = innerRadius)
    }
}