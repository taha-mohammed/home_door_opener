package com.home.door.util

import android.content.SharedPreferences
import com.home.door.data.DoorEntity

class DoorPrefs(
    private val preferences: SharedPreferences
) {

    companion object {
        private const val PREF_PREFIX_KEY = "appwidget_"
    }

    internal fun saveDoorPref(appWidgetId: Int, door: DoorEntity) {
        preferences.edit().apply {
            putString(PREF_PREFIX_KEY + appWidgetId + "_name", door.name)
            putString(PREF_PREFIX_KEY + appWidgetId + "_ip", door.ip)
            putString(PREF_PREFIX_KEY + appWidgetId + "_user", door.user)
            putString(PREF_PREFIX_KEY + appWidgetId + "_password", door.password)
            apply()
        }
    }

    internal fun loadDoorPref(appWidgetId: Int): DoorEntity {
        return preferences.let {
            DoorEntity(
                name = it.getString(PREF_PREFIX_KEY + appWidgetId + "_name", null).orEmpty(),
                ip = it.getString(PREF_PREFIX_KEY + appWidgetId + "_ip", null).orEmpty(),
                user = it.getString(PREF_PREFIX_KEY + appWidgetId + "_user", null).orEmpty(),
                password = it.getString(PREF_PREFIX_KEY + appWidgetId + "_password", null).orEmpty(),
            )
        }
    }

    internal fun deleteDoorPref(appWidgetId: Int) {
        preferences.edit().apply {
            remove(PREF_PREFIX_KEY + appWidgetId + "_name")
            remove(PREF_PREFIX_KEY + appWidgetId + "_ip")
            remove(PREF_PREFIX_KEY + appWidgetId + "_user")
            remove(PREF_PREFIX_KEY + appWidgetId + "_password")
            apply()
        }
    }
}