package com.home.door.repository.widget

import com.home.door.data.widget.Widget
import com.home.door.repository.WidgetRepo

class FakeWidgetRepo(
    private val widgets: MutableList<Widget> = mutableListOf()
): WidgetRepo {
    override suspend fun getWidget(widgetId: Int): Widget? {
        return widgets.find { it.widgetId == widgetId }
    }

    override suspend fun addWidget(widget: Widget) {
        widgets.add(widget)
    }

    override suspend fun deleteWidget(widgetId: Int) {
        widgets.removeIf { it.widgetId == widgetId }
    }

    override suspend fun deleteWidgetsByDoorId(doorId: Int): List<Int> {
        val ids = widgets.filter { it.doorId == doorId }.map { it.widgetId }
        widgets.removeIf { it.widgetId in ids }
        return ids
    }
}