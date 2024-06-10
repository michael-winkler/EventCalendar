package com.nmd.eventCalendar.shared

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nmd.eventCalendar.BuildConfig
import com.nmd.eventCalendar.model.SharedPreferencesModel
import java.lang.reflect.Type

object SharedPreferences {

    private var prefs: SharedPreferences? = null
    private const val SHARED_PREFERENCES_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
    private const val SHARED_PREFERENCES_MODEL_V1 = "$SHARED_PREFERENCES_NAME.v1"

    fun Context?.initSharedPreferences() {
        this ?: return

        if (prefs == null) {
            prefs = getSharedPreferences(
                SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
        }
    }

    fun clearSharedPreferences() {
        prefs?.edit()?.clear()?.apply()
    }

    fun saveInstanceState(sharedPreferencesModel: SharedPreferencesModel) {
        prefs?.edit()
            ?.putString(SHARED_PREFERENCES_MODEL_V1, Gson().toJson(sharedPreferencesModel))
            ?.apply()
    }

    fun restoreInstanceState(): SharedPreferencesModel {
        val json = prefs?.getString(SHARED_PREFERENCES_MODEL_V1, null)
        val type: Type = object : TypeToken<SharedPreferencesModel?>() {}.type
        return Gson().fromJson(json, type) ?: SharedPreferencesModel()
    }

}