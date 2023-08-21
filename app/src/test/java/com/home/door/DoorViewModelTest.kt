package com.home.door

import com.google.common.truth.Truth.assertThat
import com.home.door.data.room.DoorEntity
import com.home.door.main.DoorViewModel
import com.home.door.repository.door.FakeDoorRepo
import com.home.door.main.FieldErrorState
import com.home.door.main.MainEvent
import com.home.door.repository.widget.FakeWidgetRepo
import com.home.door.util.toWidget
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
        viewModel = DoorViewModel(
            FakeDoorRepo(dispatcher),
            FakeWidgetRepo()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()

    }

    @Test
    fun insertDoor() = runTest {
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
    fun deleteDoor() = runTest {
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
                validity = it.isDoorAdded
            }
        }
        viewModel.onEvent(MainEvent.AddDoor(door))

        assertThat(validity).isTrue()
    }

    @Test
    fun addWidget() = runTest {
        val door = DoorEntity(id=1 ,name = "taha", ip = "192.168.1.1", user = "taha", password = "taha")
        val widget = door.toWidget(0)

        var addedWidget: Int? = null
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                addedWidget = it.addedWidget
            }
        }

        viewModel.onEvent(MainEvent.AddDoor(door))
        viewModel.onEvent(MainEvent.AddWidget(widget))

        assertThat(addedWidget).isEqualTo(widget.widgetId)
    }

    @Test
    fun `add widgets and remove their door, widget removed`() = runTest {
        val door = DoorEntity(id=1 ,name = "taha", ip = "192.168.1.1", user = "taha", password = "taha")

        var deletedWidgets: List<Int> = listOf()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                deletedWidgets = it.deletedWidgets
            }
        }

        viewModel.onEvent(MainEvent.AddDoor(door))
        viewModel.onEvent(MainEvent.AddWidget(door.toWidget(1)))
        viewModel.onEvent(MainEvent.AddWidget(door.toWidget(2)))
        viewModel.onEvent(MainEvent.AddWidget(door.toWidget(3)))

        viewModel.onEvent(MainEvent.DeleteDoor(door))

        assertThat(deletedWidgets).containsAtLeast(1, 2, 3)
    }
}