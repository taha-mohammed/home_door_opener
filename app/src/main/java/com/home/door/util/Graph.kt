package com.home.door.util

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.home.door.data.AppDatabase
import com.home.door.repository.DoorRepoImpl

object Graph {


    private lateinit var database: AppDatabase

    val doorRepo by lazy {
        DoorRepoImpl(database.doorDao())
    }

    private lateinit var preferences: SharedPreferences

    val doorPrefs by lazy {
        DoorPrefs(preferences)
    }
    fun provide(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .build()
        preferences = context.getSharedPreferences(Constants.PREFS_NAME, 0)
    }

}