package com.home.door.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.door.data.room.DoorEntity
import com.home.door.data.widget.Widget
import com.home.door.repository.DoorRepo
import com.home.door.repository.WidgetRepo
import com.home.door.util.DoorOpener
import com.home.door.util.DoorValidator
import com.home.door.util.Graph
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val doors: List<DoorEntity> = emptyList(),
    val isDoorAdded: Boolean = false,
    val addedWidget: Int? = null,
    val deletedWidgets: List<Int> = emptyList(),
    val openResult: Result<Unit>? = null,
    val validationState: FieldErrorState = FieldErrorState()
)

class DoorViewModel(
    private val repository: DoorRepo = Graph.doorRepo,
    private val widgetRepo: WidgetRepo = Graph.widgetRepo
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
            is MainEvent.DeleteDoor -> deleteDoor(event.door)
            is MainEvent.AddWidget -> addWidget(event.widget)
            is MainEvent.UnlockDoor -> unlockDoor(event.door)
            MainEvent.ResetState -> {
                _uiState.update {
                    it.copy(
                        isDoorAdded = false,
                        addedWidget = null,
                        validationState = FieldErrorState(),
                        openResult = null,
                        deletedWidgets = emptyList()
                    )
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
                previous.copy(isDoorAdded = true)
            }
        }
    }

    private fun deleteDoor(door: DoorEntity) {
        viewModelScope.launch {
            repository.deleteDoor(door)
            widgetRepo.deleteWidgetsByDoorId(door.id).let {
                _uiState.update {previous ->
                    previous.copy(deletedWidgets = it)
                }
            }
        }
    }

    private fun addWidget(widget: Widget) {
        viewModelScope.launch {
            try {
                widgetRepo.addWidget(widget)
                _uiState.update {previous ->
                    previous.copy(addedWidget = widget.widgetId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
