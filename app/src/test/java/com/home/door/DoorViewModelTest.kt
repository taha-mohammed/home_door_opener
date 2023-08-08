package com.home.door

import com.google.common.truth.Truth.assertThat
import com.home.door.data.room.DoorEntity
import com.home.door.main.DoorViewModel
import com.home.door.repository.door.FakeDoorRepo
import com.home.door.main.FieldErrorState
import com.home.door.main.MainEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class DoorViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: DoorViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = DoorViewModel(FakeDoorRepo(dispatcher))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun insert() = runTest {
        viewModel.initialize()
        val door = DoorEntity(id=0 ,name = "door1", ip = "192.168.1.1", user = "taha", password = "taha")

        var doors = listOf<DoorEntity>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect{
                doors = it.doors
            }
        }
        viewModel.onEvent(MainEvent.AddDoor(door))

        assertThat(doors.first().name).isEqualTo("door1")
    }

    @Test
    fun delete() = runTest {
        viewModel.initialize()
        val door = DoorEntity(id=0 ,name = "door1", ip = "192.168.1.1", user = "taha", password = "taha")

        var doors = listOf<DoorEntity>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect{
                doors = it.doors
            }
        }
        viewModel.onEvent(MainEvent.AddDoor(door))

        viewModel.onEvent(MainEvent.DeleteDoor(door))

        assertThat(doors).isEmpty()

    }

    @Test
    fun `add door with empty name,return error message`() = runTest {
        val door = DoorEntity(id=0 ,name = "", ip = "192.168.1.1", user = "taha", password = "taha")

        var errorState = FieldErrorState()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect{
                errorState = it.validationState
            }
        }
        viewModel.onEvent(MainEvent.AddDoor(door))

        assertThat(errorState.nameError).isNotNull()
    }

    @Test
    fun `add door with valid fields,return true`() = runTest {
        val door = DoorEntity(id=0 ,name = "taha", ip = "192.168.1.1", user = "taha", password = "taha")

        var validity: Boolean? = null
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect{
                validity = it.isAdded
            }
        }
        viewModel.onEvent(MainEvent.AddDoor(door))

        assertThat(validity).isTrue()
    }
}