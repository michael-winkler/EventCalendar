package com.nmd.eventCalendar

import android.app.Application
import android.content.res.Resources

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        appResources = resources
    }

    companion object {
        private var appResources: Resources? = null

        /*
        Keep if needed in future
         */
        @Suppress("unused")
        fun Int.getDimensInt(): Int {
            return appResources?.getDimensionPixelSize(this) ?: 0
        }

    }

}