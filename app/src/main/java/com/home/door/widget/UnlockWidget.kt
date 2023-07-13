package com.home.door.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.home.door.R
import com.home.door.data.DoorEntity
import com.home.door.util.DoorPrefs
import com.home.door.util.toList


class UnlockWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            DoorPrefs.deleteDoorPref(context, appWidgetId)
        }
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val door = DoorPrefs.loadDoorPref(context, appWidgetId)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.unlock_widget).apply {
        setTextViewText(R.id.appwidget_text, door.name)
        setOnClickPendingIntent(R.id.appwidget, getPendingIntent(context, door) )
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun getPendingIntent(context: Context, door: DoorEntity): PendingIntent {
    val intent = Intent(context, WidgetActionReceiver::class.java)
    intent.action = "ClickAction"
    intent.putExtra("door", door.toList().toTypedArray())

    return PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}