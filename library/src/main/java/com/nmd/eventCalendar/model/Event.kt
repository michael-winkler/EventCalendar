package com.nmd.eventCalendar.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
data class Event(
    val date: String,
    val name: String,
    val backgroundHexColor: String,
    val data: @RawValue Any? = null,
    val timeRange: EventTimeRange? = null
) : Parcelable