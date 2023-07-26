package com.home.door.util

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.home.door.data.AppDatabase
import com.home.door.repository.DoorRepoImpl

object Graph {

    private const val PREFS_NAME = "com.home.door.widget.UnlockWidget"

    private lateinit var database: AppDatabase

    val doorRepo by lazy {
        DoorRepoImpl(database.doorDao())
    }

    private lateinit var preferences: SharedPreferences

    val doorPrefs by lazy {
        DoorPrefs(preferences)
    }
    fun provide(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, "data.db")
            .build()
        preferences = context.getSharedPreferences(PREFS_NAME, 0)
    }

}