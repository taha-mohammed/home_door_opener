package com.home.door.main

import com.home.door.data.room.DoorEntity

sealed class MainEvent {
    data class DeleteDoor(val door: DoorEntity): MainEvent()
    data class AddDoor(val door: DoorEntity): MainEvent()
    data class UnlockDoor(val door: DoorEntity): MainEvent()
    data class PinDoorWidget(val door: DoorEntity): MainEvent()
    object DoorAdded: MainEvent()
    object DoorUnlocked: MainEvent()
}