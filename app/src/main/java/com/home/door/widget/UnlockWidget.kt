package com.home.door.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.home.door.R
import com.home.door.data.widget.Widget
import com.home.door.util.Constants
import com.home.door.util.Graph
import com.home.door.util.toDoor
import com.home.door.util.toList
import kotlinx.coroutines.runBlocking


class UnlockWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        runBlocking {
            for (appWidgetId in appWidgetIds) {
                Graph.widgetRepo.deleteWidget(appWidgetId)
            }
        }
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val widget = runBlocking {
        Graph.widgetRepo.getWidget(appWidgetId)
    }
    widget?.let {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.unlock_widget).apply {
            setTextViewText(R.id.appwidget_text, it.doorName)
            setOnClickPendingIntent(R.id.appwidget, getPendingIntent(context, it) )
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}

internal fun invalidateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: List<Int>) {
    val views = RemoteViews(context.packageName, R.layout.invalid_widget)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetIds.toIntArray(), views)
}

private fun getPendingIntent(context: Context, widget: Widget): PendingIntent {
    val intent = Intent(context, WidgetActionActivity::class.java)
    intent.action = Constants.WIDGET_CLICK_ACTION+widget.widgetId
    intent.putExtra(Constants.EXTRA_DOOR, widget.toDoor().toList().toTypedArray())

    return PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}