package com.home.door.main

import com.home.door.data.room.DoorEntity
import com.home.door.data.widget.Widget

sealed class MainEvent {
    data class DeleteDoor(val door: DoorEntity): MainEvent()
    data class AddDoor(val door: DoorEntity): MainEvent()
    data class UnlockDoor(val door: DoorEntity): MainEvent()
    data class AddWidget(val widget: Widget): MainEvent()
    object ResetState: MainEvent()
}