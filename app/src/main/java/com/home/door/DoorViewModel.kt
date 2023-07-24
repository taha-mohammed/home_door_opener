package com.home.door

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.door.data.DoorEntity
import com.home.door.data.DoorRepo
import com.home.door.util.Graph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DoorViewModel(
    private val repository: DoorRepo = Graph.doorRepo
) : ViewModel() {


    var doors: List<DoorEntity> = emptyList()
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.getDoors().collect {
                doors = it
                _uiEvent.send(UiEvent.REFRESH)
            }
        }
    }
    fun insert(door: DoorEntity) {
        viewModelScope.launch {
            repository.insertDoors(listOf(door))
        }
    }

    fun delete(door: DoorEntity) {
        viewModelScope.launch {
            repository.deleteDoor(door)
        }
    }
}

enum class UiEvent{
    REFRESH,
    NONE
}