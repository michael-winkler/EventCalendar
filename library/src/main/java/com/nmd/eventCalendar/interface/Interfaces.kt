package com.nmd.eventCalendar.`interface`

import com.nmd.eventCalendar.model.Day

/**
 * Interface for click events.
 */
interface EventCalendarDayClickListener {
    /**
     * @param day [Day]
     */
    fun onClick(day: Day)
}

/**
 * Interface for scroll events.
 */
interface EventCalendarScrollListener {
    /**
     * @param month [Int] eg. January is 1, February...
     * @param year [Int] eg. 2022
     */
    fun onScrolled(month: Int, year: Int)
}