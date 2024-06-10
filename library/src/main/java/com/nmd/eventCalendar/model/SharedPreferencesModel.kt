package com.nmd.eventCalendar.model

import java.util.Calendar

class SharedPreferencesModel(
    var disallowIntercept: Boolean = true,
    var calendarWeekVisible: Boolean = false,
    var currentRecyclerViewPosition: Int = 0,
    var startMonth: Int = Calendar.JANUARY,
    var startYear: Int = 2020,
    var endMonth: Int = Calendar.DECEMBER,
    var endYear: Int = Calendar.getInstance().get(Calendar.YEAR).plus(1),
    var currentYearAndMonthPair: Pair<Int, Int> = Pair(0, 0),
    var events: ArrayList<Event> = arrayListOf()
)