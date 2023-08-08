package com.home.door.util

import android.content.SharedPreferences
import com.home.door.data.DoorEntity

class DoorPrefs(
    private val preferences: SharedPreferences
) {

    internal fun saveDoorPref(appWidgetId: Int, door: DoorEntity) {
        preferences.edit().apply {
            putString(Constants.PREF_PREFIX_KEY + appWidgetId + "_name", door.name)
            putString(Constants.PREF_PREFIX_KEY + appWidgetId + "_ip", door.ip)
            putString(Constants.PREF_PREFIX_KEY + appWidgetId + "_user", door.user)
            putString(Constants.PREF_PREFIX_KEY + appWidgetId + "_password", door.password)
            apply()
        }
    }

    internal fun loadDoorPref(appWidgetId: Int): DoorEntity {
        return preferences.let {
            DoorEntity(
                name = it.getString(Constants.PREF_PREFIX_KEY + appWidgetId + "_name", null).orEmpty(),
                ip = it.getString(Constants.PREF_PREFIX_KEY + appWidgetId + "_ip", null).orEmpty(),
                user = it.getString(Constants.PREF_PREFIX_KEY + appWidgetId + "_user", null).orEmpty(),
                password = it.getString(Constants.PREF_PREFIX_KEY + appWidgetId + "_password", null).orEmpty(),
            )
        }
    }

    internal fun deleteDoorPref(appWidgetId: Int) {
        preferences.edit().apply {
            remove(Constants.PREF_PREFIX_KEY + appWidgetId + "_name")
            remove(Constants.PREF_PREFIX_KEY + appWidgetId + "_ip")
            remove(Constants.PREF_PREFIX_KEY + appWidgetId + "_user")
            remove(Constants.PREF_PREFIX_KEY + appWidgetId + "_password")
            apply()
        }
    }
}