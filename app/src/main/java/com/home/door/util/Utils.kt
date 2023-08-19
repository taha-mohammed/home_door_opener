package com.home.door.util

import com.home.door.data.room.DoorEntity
import com.home.door.data.widget.Widget

fun DoorEntity.toList(): List<String> {
    return listOf(id.toString(), name, ip, user, password)
}

fun List<String>.toDoor(): DoorEntity {
    return DoorEntity(
        id = get(0).toInt(),
        name = get(1),
        ip = get(2),
        user = get(3),
        password = get(4)
    )
}

fun DoorEntity.toWidget(widgetId: Int) =
    Widget(
        widgetId = widgetId,
        doorId = id,
        doorName = name,
        doorIp = ip,
        doorUser = user,
        doorPassword = password
    )

fun Widget.toDoor() =
    DoorEntity(
        id = doorId,
        ip = doorIp,
        name = doorName,
        user = doorUser,
        password = doorPassword
    )