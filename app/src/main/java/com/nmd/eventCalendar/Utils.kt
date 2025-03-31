package com.nmd.eventCalendar

import androidx.core.graphics.ColorUtils

class Utils {

    companion object {

        fun Int.isDarkColor(): Boolean {
            return ColorUtils.calculateLuminance(this) < 0.5
        }

    }

}