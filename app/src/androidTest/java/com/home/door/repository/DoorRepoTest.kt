package com.home.door.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.home.door.data.room.AppDatabase
import com.home.door.data.room.DoorEntity
import com.home.door.repository.door.DefaultDoorRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class DoorRepoTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repo: DoorRepo
    private lateinit var database: AppDatabase

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        repo = DefaultDoorRepo(
            database.doorDao(),
            dispatcher
        )
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertDoor() = runTest(dispatcher.scheduler) {
        val door =
            DoorEntity(id = 0, name = "door1", ip = "192.168.1.1", user = "taha", password = "taha")
        repo.insertDoors(listOf(door))

        val doors = repo.getDoors().first()

        Truth.assertThat(doors.first().name).isEqualTo("door1")
    }

    @Test
    fun updateDoor() = runTest(dispatcher.scheduler) {
        DoorEntity(
            id = 0,
            name = "door1",
            ip = "192.168.1.1",
            user = "taha",
            password = "taha"
        ).let {
            repo.insertDoors(listOf(it))
        }

        repo.getDoors().first().first().copy(name = "updated_door1").let {
            repo.updateDoors(listOf(it))
        }

        val doors = repo.getDoors().first()

        Truth.assertThat(doors.first().name).isEqualTo("updated_door1")
    }

    @Test
    fun deleteDoor() = runTest(dispatcher.scheduler) {
        DoorEntity(
            id = 0,
            name = "door1",
            ip = "192.168.1.1",
            user = "taha",
            password = "taha"
        ).let {
            repo.insertDoors(listOf(it))
        }

        repo.getDoors().first().first().let {
            repo.deleteDoor(it)
        }

        val doors = repo.getDoors().first()

        Truth.assertThat(doors).isEmpty()
    }
}