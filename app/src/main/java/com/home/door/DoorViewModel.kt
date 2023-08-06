package com.home.door

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.door.data.DoorEntity
import com.home.door.data.DoorRepo
import com.home.door.util.DoorOpener
import com.home.door.util.DoorValidator
import com.home.door.util.FieldErrorState
import com.home.door.util.Graph
import com.home.door.util.MainEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val doors: List<DoorEntity> = emptyList(),
    val isAdded: Boolean = false,
    val openResult: Result<Unit>? = null,
    val validationState: FieldErrorState = FieldErrorState()
)

class DoorViewModel(
    private val repository: DoorRepo = Graph.doorRepo
) : ViewModel() {


    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private var initJob: Job? = null

    fun initialize() {
        if (initJob != null)
            return
        initJob = viewModelScope.launch {
            repository.getDoors().collect {
                _uiState.update {previous ->
                    previous.copy(doors = it)
                }
            }
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.AddDoor -> addDoor(event.door)
            is MainEvent.DeleteDoor -> delete(event.door)
            is MainEvent.PinDoorWidget -> TODO()
            is MainEvent.UnlockDoor -> unlockDoor(event.door)
            MainEvent.DoorAdded -> {
                _uiState.update {
                    it.copy(isAdded = false, validationState = FieldErrorState())
                }
            }
            MainEvent.DoorUnlocked -> {
                _uiState.update {
                    it.copy(openResult = null)
                }
            }
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
                _uiState.update {previous ->
                    previous.copy(validationState = errorState)
                }
                return@launch
            }
            repository.insertDoors(listOf(door))
            _uiState.update {previous ->
                previous.copy(isAdded = true)
            }
        }
    }

    private fun delete(door: DoorEntity) {
        viewModelScope.launch {
            repository.deleteDoor(door)
        }
    }

    private fun unlockDoor(door: DoorEntity) {
        viewModelScope.launch {
            DoorOpener.unlockDoor(door).also {
                _uiState.update {previous ->
                    previous.copy(openResult = it)
                }
            }
        }
    }
}
