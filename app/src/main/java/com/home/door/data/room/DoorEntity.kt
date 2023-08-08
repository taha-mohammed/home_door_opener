package com.home.door.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "door")
data class DoorEntity(
    @PrimaryKey(true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val ip: String,
    @ColumnInfo val user: String,
    @ColumnInfo val password: String
)