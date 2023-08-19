package com.home.door.data.widget

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import okhttp3.internal.immutableListOf

@Serializable
data class AppWidgets(
    val widgets: List<Widget> = listOf()
)

@Serializable
data class Widget(
    val widgetId: Int,
    val doorId: Int,
    val doorName: String,
    val doorIp: String,
    val doorUser: String,
    val doorPassword: String,
)