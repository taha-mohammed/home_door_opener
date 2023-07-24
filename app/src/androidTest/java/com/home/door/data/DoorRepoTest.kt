package com.home.door.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class DoorRepoTest {

    private lateinit var repo: DoorRepo
    private lateinit var database: AppDatabase

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        repo = DoorRepoImpl(database.doorDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertDoor() = runTest {
        val door = DoorEntity(id=0 ,name = "door1", ip = "192.168.1.1", user = "taha", password = "taha")
        repo.insertDoors(listOf(door))

        val doors = repo.getDoors().first()

        assertThat(doors.first().name).isEqualTo("door1")
    }

    @Test
    fun updateDoor() = runTest {
        DoorEntity(id=0 ,name = "door1", ip = "192.168.1.1", user = "taha", password = "taha").let {
            repo.insertDoors(listOf(it))
        }

        repo.getDoors().first().first().copy( name = "updated_door1" ).let {
            repo.updateDoors(listOf(it))
        }

        val doors = repo.getDoors().first()

        assertThat(doors.first().name).isEqualTo("updated_door1")
    }

    @Test
    fun deleteDoor() = runTest {
        DoorEntity(id=0 ,name = "door1", ip = "192.168.1.1", user = "taha", password = "taha").let {
            repo.insertDoors(listOf(it))
        }

        repo.getDoors().first().first().let {
            repo.deleteDoor(it)
        }

        val doors = repo.getDoors().first()

        assertThat(doors).isEmpty()
    }
}