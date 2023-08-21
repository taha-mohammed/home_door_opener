package com.home.door.data.widget


import kotlinx.serialization.Serializable

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