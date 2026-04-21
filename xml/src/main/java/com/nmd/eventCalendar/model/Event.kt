package com.nmd.eventCalendar.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Keep
@Serializable
data class Event(
    val date: String,
    val name: String,
    val backgroundHexColor: String,
    @Transient
    val data: Any? = null,
    val timeRange: EventTimeRange? = null
)