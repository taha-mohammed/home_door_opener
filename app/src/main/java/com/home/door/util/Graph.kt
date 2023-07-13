package com.home.door.util

import android.content.Context
import androidx.room.Room
import com.home.door.data.AppDatabase
import com.home.door.data.DoorRepoImpl

object Graph {
    lateinit var database: AppDatabase
        private set

    val doorRepo by lazy {
        DoorRepoImpl(database.doorDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, "data.db")
            .build()
    }

}