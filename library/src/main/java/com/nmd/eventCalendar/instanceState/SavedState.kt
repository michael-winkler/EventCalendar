package com.nmd.eventCalendar.instanceState

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View

@Suppress("unused")
class SavedState : View.BaseSavedState {
    val state: Bundle

    constructor(superState: Parcelable?, state: Bundle) : super(superState) {
        this.state = state
    }

    constructor(parcel: Parcel) : super(parcel) {
        state = parcel.readBundle(javaClass.classLoader)!!
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeBundle(state)
    }

    companion object {
        const val SAVED_STATE_DISALLOW_INTERCEPT = "disallowIntercept"
        const val SAVED_STATE_CALENDAR_WEEK_VISIBLE = "calendarWeekVisible"
        const val SAVED_STATE_RECYCLERVIEW_POSITION = "position"
        const val SAVED_STATE_START_MONTH = "start_month"
        const val SAVED_STATE_START_YEAR = "start_year"
        const val SAVED_STATE_END_MONTH = "end_month"
        const val SAVED_STATE_END_YEAR = "end_year"
        const val SAVED_STATE_EVENTS = "events"
        const val SAVED_STATE_YEAR_AND_MONTH_PAIR = "year_and_month_pair"

        @JvmField
        val CREATOR = object : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }


    }
}