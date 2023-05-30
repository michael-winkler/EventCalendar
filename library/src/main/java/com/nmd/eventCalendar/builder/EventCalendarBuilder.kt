package com.nmd.eventCalendar.builder

import android.os.Bundle
import com.nmd.eventCalendar.EventCalendarView
import com.nmd.eventCalendar.fragment.EventCalendarFragment
import java.util.Calendar

class EventCalendarBuilder {

    companion object {
        const val BUILDER_MONTH = "calendar_month"
        const val BUILDER_YEAR = "calendar_year"
    }

    private var month: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var year: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var eventCalendarView: EventCalendarView? = null

    fun date(month: Int, year: Int): EventCalendarBuilder {
        this.month = month
        this.year = year
        return this
    }

    fun eventCalendarView(eventCalendarView: EventCalendarView): EventCalendarBuilder {
        this.eventCalendarView = eventCalendarView
        return this
    }

    fun build(): EventCalendarFragment {
        if (eventCalendarView == null) {
            throw RuntimeException("EventCalendarView must be set in EventCalendarBuilder.")
        }

        val eventCalendarFragment = EventCalendarFragment()
        eventCalendarFragment.eventCalendarView = eventCalendarView
        eventCalendarFragment.arguments = Bundle().apply {
            putInt(BUILDER_MONTH, month)
            putInt(BUILDER_YEAR, year)
        }
        return eventCalendarFragment
    }

}