package com.home.door.util

import android.content.Context
import com.home.door.data.DoorEntity

object DoorPrefs {

    private const val PREFS_NAME = "com.home.door.widget.UnlockWidget"
    private const val PREF_PREFIX_KEY = "appwidget_"

    internal fun saveDoorPref(context: Context, appWidgetId: Int, door: DoorEntity) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
        prefs.apply {
            putString(PREF_PREFIX_KEY + appWidgetId + "_name", door.name)
            putString(PREF_PREFIX_KEY + appWidgetId + "_ip", door.ip)
            putString(PREF_PREFIX_KEY + appWidgetId + "_user", door.user)
            putString(PREF_PREFIX_KEY + appWidgetId + "_password", door.password)
        }
        prefs.apply()
    }

    internal fun loadDoorPref(context: Context, appWidgetId: Int): DoorEntity {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return DoorEntity(
            name = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_name", null).orEmpty(),
            ip = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_ip", null).orEmpty(),
            user = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_user", null).orEmpty(),
            password = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_password", null).orEmpty(),
        )
    }

    internal fun deleteDoorPref(context: Context, appWidgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_name")
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_ip")
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_user")
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_password")
        prefs.apply()
    }
}