package com.nmd.eventCalendar.interfaces

import com.nmd.eventCalendar.model.Day

/**
 * Interface definition for a callback to be invoked when a day in the calendar is clicked.
 */
interface EventCalendarDayClickListener {
    /**
     * Called when a day has been clicked.
     *
     * @param day The [Day] object containing information about the clicked date and its events.
     */
    fun onClick(day: Day)
}

/**
 * Interface definition for a callback to be invoked when the calendar is scrolled to a new month.
 */
interface EventCalendarScrollListener {
    /**
     * Called when the calendar has been scrolled to a new month.
     *
     * @param month The new month (1 = January, 12 = December).
     * @param year The new year (e.g., 2023).
     */
    fun onScrolled(month: Int, year: Int)
}