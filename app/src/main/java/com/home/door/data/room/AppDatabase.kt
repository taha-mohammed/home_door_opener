package com.home.door.data.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [DoorEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doorDao(): DoorDao

}
