package com.home.door

import com.home.door.data.DoorEntity
import com.home.door.data.FakeDoorRepoImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
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
        viewModel = DoorViewModel(FakeDoorRepoImpl(dispatcher))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun insert() = runTest {
        val door = DoorEntity(id=0 ,name = "door1", ip = "192.168.1.1", user = "taha", password = "taha")

        var doors = listOf<DoorEntity>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.collect{
                when (it) {
                    UiEvent.REFRESH -> {
                        doors = viewModel.doors
                    }
                    UiEvent.NONE -> {}
                }
            }
        }
        viewModel.insert(door)

        assertThat(doors.first().name).isEqualTo("door1")
    }

    @Test
    fun delete() = runTest {
        val door = DoorEntity(id=0 ,name = "door1", ip = "192.168.1.1", user = "taha", password = "taha")

        var doors = listOf<DoorEntity>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.collect{
                when (it) {
                    UiEvent.REFRESH -> {
                        doors = viewModel.doors
                    }
                    UiEvent.NONE -> {}
                }
            }
        }
        viewModel.insert(door)

        viewModel.delete(door)

        assertThat(doors).isEmpty()

    }
}