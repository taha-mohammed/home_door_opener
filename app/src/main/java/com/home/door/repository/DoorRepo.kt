package com.home.door.repository

import com.home.door.data.room.DoorEntity
import kotlinx.coroutines.flow.Flow

interface DoorRepo {
    fun getDoors(): Flow<List<DoorEntity>>
    suspend fun getDoor(id: String): DoorEntity
    suspend fun insertDoors(doors: List<DoorEntity>)
    suspend fun updateDoors(doors: List<DoorEntity>)
    suspend fun deleteDoor(door: DoorEntity)
}