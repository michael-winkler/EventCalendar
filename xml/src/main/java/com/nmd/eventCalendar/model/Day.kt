package com.nmd.eventCalendar.model

import androidx.annotation.Keep

@Keep
data class Day(
    /**
     * eg: 31
     */
    val value: String,
    /**
     * eg: true | false
     */
    val isCurrentMonth: Boolean,
    /**
     * eg: true | false
     */
    var isCurrentDay: Boolean,
    /**
     * eg: 31.12.2024
     */
    var date: String,
)