package com.nmd.eventCalendar.compose.ui

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.WeekItemPosition

/**
 * Prebuilt shapes for week number items to avoid allocating new shapes on every recomposition.
 */
data class WeekItemShapes(
    val outerRadius: Dp = 50.dp,
    val innerRadius: Dp = 4.dp,
    val top: RoundedCornerShape = RoundedCornerShape(
        topStart = CornerSize(outerRadius),
        topEnd = CornerSize(outerRadius),
        bottomStart = CornerSize(innerRadius),
        bottomEnd = CornerSize(innerRadius)
    ),
    val middle: RoundedCornerShape = RoundedCornerShape(innerRadius),
    val bottom: RoundedCornerShape = RoundedCornerShape(
        topStart = CornerSize(innerRadius),
        topEnd = CornerSize(innerRadius),
        bottomStart = CornerSize(outerRadius),
        bottomEnd = CornerSize(outerRadius)
    )
) {
    fun forPosition(position: WeekItemPosition): RoundedCornerShape = when (position) {
        WeekItemPosition.Top -> top
        WeekItemPosition.Middle -> middle
        WeekItemPosition.Bottom -> bottom
    }
}

@Composable
fun rememberWeekItemShapes(
    outerRadius: Dp = 50.dp,
    innerRadius: Dp = 4.dp
): WeekItemShapes {
    return remember(outerRadius, innerRadius) {
        WeekItemShapes(outerRadius = outerRadius, innerRadius = innerRadius)
    }
}