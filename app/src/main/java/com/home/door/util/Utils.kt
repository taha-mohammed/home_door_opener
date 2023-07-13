package com.home.door.util

import com.home.door.data.DoorEntity

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