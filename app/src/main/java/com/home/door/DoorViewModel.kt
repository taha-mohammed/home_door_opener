package com.home.door

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.door.data.DoorEntity
import com.home.door.data.DoorRepo
import com.home.door.util.*
import kotlinx.coroutines.cancel
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
                _uiEvent.send(UiEvent.Refresh)
            }
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.AddDoor -> addDoor(event.door)
            is MainEvent.DeleteDoor -> delete(event.door)
            is MainEvent.PinDoorWidget -> TODO()
            is MainEvent.UnlockDoor -> unlockDoor(event.door)
        }
    }
    private fun addDoor(door: DoorEntity) {
        viewModelScope.launch {
            val errorState = DoorValidator.validateDoor(door)
            val isError = listOf(
                errorState.nameError,
                errorState.ipError,
                errorState.userError,
                errorState.passwordError
            ).any { it != null }
            if (isError) {
                _uiEvent.send(UiEvent.ValidateFields(errorState))
                return@launch
            }
            repository.insertDoors(listOf(door))
            _uiEvent.send(UiEvent.AddSuccess)
        }
    }

    private fun delete(door: DoorEntity) {
        viewModelScope.launch {
            repository.deleteDoor(door)
        }
    }

    private fun unlockDoor(door: DoorEntity) {
        viewModelScope.launch {
            DoorOpener.unlockDoor(door)
        }
    }
}

sealed class UiEvent{
    object Refresh: UiEvent()
    object AddSuccess: UiEvent()
    data class ValidateFields(val result: FieldErrorState): UiEvent()
}