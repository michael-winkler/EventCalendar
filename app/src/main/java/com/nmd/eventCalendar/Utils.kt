package com.nmd.eventCalendar

import android.content.Context

class Utils {

    companion object {

        fun Int.getMonthName(context: Context?): String {
            context ?: return ""
            val array = context.resources.getStringArray(R.array.ecv_month_names)
            return array[this]
        }

    }

}