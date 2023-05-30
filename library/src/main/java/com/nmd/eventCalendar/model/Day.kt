package com.nmd.eventCalendar.model

data class Day(
    val value: String,
    val isCurrentMonth: Boolean,
    var isCurrentDay: Boolean,
    var date: String,
)