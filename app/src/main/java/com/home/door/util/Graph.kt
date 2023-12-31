package com.home.door.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.home.door.data.room.AppDatabase
import com.home.door.data.widget.AppWidgets
import com.home.door.data.widget.AppWidgetsSerializer
import com.home.door.repository.door.DefaultDoorRepo
import com.home.door.repository.widget.DefaultWidgetRepo

object Graph {

    private lateinit var database: AppDatabase
    val doorRepo by lazy {
        DefaultDoorRepo(database.doorDao())
    }

    private lateinit var dataStore: DataStore<AppWidgets>
    val widgetRepo by lazy {
        DefaultWidgetRepo(dataStore)
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .build()
        dataStore = DataStoreFactory.create(
            serializer = AppWidgetsSerializer
        ) {
            context.dataStoreFile(Constants.DATASTORE_FILE_NAME)
        }
    }

}