package com.home.door.repository.door

import com.home.door.data.room.DoorEntity
import com.home.door.repository.DoorRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

class FakeDoorRepo(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): DoorRepo {

    private val doorList = mutableListOf<DoorEntity>()
    private val flow = MutableSharedFlow<List<DoorEntity>>()

    override fun getDoors(): Flow<List<DoorEntity>> = flow

    override suspend fun getDoor(id: String): DoorEntity = withContext(ioDispatcher) {
        doorList.first { it.id == id.toInt() }
    }

    override suspend fun insertDoors(doors: List<DoorEntity>) = withContext(ioDispatcher) {
        doorList.addAll(doors)
        flow.emit(doorList)
    }

    override suspend fun updateDoors(doors: List<DoorEntity>) = withContext(ioDispatcher) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDoor(door: DoorEntity) = withContext(ioDispatcher) {
        doorList.removeIf{ it.id == door.id }
        flow.emit(doorList)
    }
}