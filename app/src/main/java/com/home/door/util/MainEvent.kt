package com.home.door.util

import com.home.door.data.DoorEntity

sealed class MainEvent {
    data class DeleteDoor(val door: DoorEntity): MainEvent()
    data class AddDoor(val door: DoorEntity): MainEvent()
    data class UnlockDoor(val door: DoorEntity): MainEvent()
    data class PinDoorWidget(val door: DoorEntity): MainEvent()
}