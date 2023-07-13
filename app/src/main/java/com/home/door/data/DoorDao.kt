package com.home.door.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface DoorDao {

    @Query("SELECT * FROM door")
    fun getDoors(): Flow<List<DoorEntity>>

    @Query("SELECT * FROM door WHERE door.id = :id")
    suspend fun getDoor(id: String): DoorEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDoors(doors: List<DoorEntity>)

    @Update
    suspend fun updateDoors(doors: List<DoorEntity>)

    @Delete
    suspend fun deleteDoor(door: DoorEntity)

}