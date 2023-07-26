package com.home.door.repository

import com.home.door.data.DoorDao
import com.home.door.data.DoorEntity
import com.home.door.data.DoorRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DoorRepoImpl constructor(
    private val doorDao: DoorDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DoorRepo {
    override fun getDoors(): Flow<List<DoorEntity>> {
        return doorDao.getDoors().flowOn(ioDispatcher)
    }

    override suspend fun getDoor(id: String): DoorEntity = withContext(ioDispatcher) {
        doorDao.getDoor(id)
    }

    override suspend fun insertDoors(doors: List<DoorEntity>) = withContext(ioDispatcher) {
        doorDao.insertDoors(doors)
    }

    override suspend fun updateDoors(doors: List<DoorEntity>) = withContext(ioDispatcher) {
        doorDao.updateDoors(doors)
    }

    override suspend fun deleteDoor(door: DoorEntity) = withContext(ioDispatcher) {
        doorDao.deleteDoor(door)
    }

}