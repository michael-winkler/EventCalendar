package com.nmd.eventCalendar.compose.model

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import java.time.LocalDate

@Keep
data class Event(
    val date: LocalDate,
    val name: String,
    val shapeColor: Color,
    val textColor: Color,
    val data: Any? = null,
    val timeRange: EventTimeRange? = null
)