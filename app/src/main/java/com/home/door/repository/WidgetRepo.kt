package com.home.door.repository

import com.home.door.data.widget.Widget

interface WidgetRepo {
    suspend fun getWidget(widgetId: Int): Widget?
    suspend fun addWidget(widget: Widget)
    suspend fun deleteWidget(widgetId: Int)
    suspend fun deleteWidgetsByDoorId(doorId: Int): List<Int>
}