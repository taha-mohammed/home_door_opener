package com.home.door.repository.widget

import android.util.Log
import androidx.datastore.core.DataStore
import com.home.door.data.widget.AppWidgets
import com.home.door.data.widget.Widget
import com.home.door.repository.WidgetRepo
import com.home.door.util.Graph
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DefaultWidgetRepo(
    private val dataStore: DataStore<AppWidgets>
): WidgetRepo {
    override suspend fun getWidget(widgetId: Int): Widget? {
        return dataStore.data.first().widgets.find {
            it.widgetId == widgetId
        }.also {
            Log.d("Repo", "getWidget: $it")
        }
    }

    override suspend fun addWidget(widget: Widget): Unit {
        dataStore.updateData {
            AppWidgets(it.widgets + widget)
        }.also {
            Log.d("Repo", "widget added:$it")
        }
    }

    override suspend fun deleteWidget(widgetId: Int): Unit {
        dataStore.updateData { appWidgets ->
            AppWidgets(appWidgets.widgets.dropWhile { it.widgetId == widgetId }.toPersistentList())
        }
    }

    override suspend fun deleteWidgetsByDoorId(doorId: Int): List<Int> {
        val widgetIds = dataStore.data.first().widgets
            .filter { it.doorId == doorId }
            .map { it.widgetId }
        dataStore.updateData { appWidgets ->
            AppWidgets(appWidgets.widgets.dropWhile { it.widgetId in widgetIds }.toPersistentList())
        }
        return widgetIds
    }
}