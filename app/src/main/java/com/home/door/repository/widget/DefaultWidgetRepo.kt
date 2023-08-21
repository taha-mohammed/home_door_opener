package com.home.door.repository.widget

import androidx.datastore.core.DataStore
import com.home.door.data.widget.AppWidgets
import com.home.door.data.widget.Widget
import com.home.door.repository.WidgetRepo
import kotlinx.coroutines.flow.first

class DefaultWidgetRepo(
    private val dataStore: DataStore<AppWidgets>
): WidgetRepo {
    override suspend fun getWidget(widgetId: Int): Widget? {
        return dataStore.data.first().widgets.find {
            it.widgetId == widgetId
        }
    }

    override suspend fun addWidget(widget: Widget) {
        dataStore.updateData { appWidgets ->
            appWidgets.widgets.filter { it.widgetId != widget.widgetId }.run {
                AppWidgets(this + widget)
            }
        }
    }

    override suspend fun deleteWidget(widgetId: Int) {
        dataStore.updateData { appWidgets ->
            AppWidgets(appWidgets.widgets.filter { it.widgetId != widgetId })
        }
    }

    override suspend fun deleteWidgetsByDoorId(doorId: Int): List<Int> {
        val widgetIds = dataStore.data.first().widgets
            .filter { it.doorId == doorId }
            .map { it.widgetId }
        dataStore.updateData { appWidgets ->
            AppWidgets(appWidgets.widgets.filter { it.widgetId !in widgetIds })
        }
        return widgetIds
    }
}