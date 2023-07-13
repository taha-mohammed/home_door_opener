package com.home.door.data

import android.app.Application
import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class DoorRepoImpl constructor(
    private val doorDao: DoorDao
) : DoorRepo {
    override fun getDoors(): Flow<List<DoorEntity>> {
        return doorDao.getDoors()
    }

    override suspend fun getDoor(id: String): DoorEntity {
        return doorDao.getDoor(id)
    }

    override suspend fun insertDoors(doors: List<DoorEntity>) {
        doorDao.insertDoors(doors)
    }

    override suspend fun updateDoors(doors: List<DoorEntity>) {
        doorDao.updateDoors(doors)
    }

    override suspend fun deleteDoor(door: DoorEntity) {
        doorDao.deleteDoor(door)
    }

}