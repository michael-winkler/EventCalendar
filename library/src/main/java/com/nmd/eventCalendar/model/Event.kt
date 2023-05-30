package com.nmd.eventCalendar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Event(
    val date: String,
    val name: String,
    val backgroundHexColor: String,
    val data: @RawValue Any? = null,
) : Parcelable