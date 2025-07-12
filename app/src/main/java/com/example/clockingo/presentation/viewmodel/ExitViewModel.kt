package com.example.clockingo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockingo.domain.model.Exit
import com.example.clockingo.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExitViewModel(
    private val getAllExitsUseCase: GetAllExitsUseCase,
    private val getExitByIdUseCase: GetExitByIdUseCase,
    private val createExitUseCase: CreateExitUseCase,
    private val updateExitUseCase: UpdateExitUseCase,
    private val deleteExitUseCase: DeleteExitUseCase
) : ViewModel() {

    private val _exitList = MutableStateFlow<List<Exit>>(emptyList())
    val exitList: StateFlow<List<Exit>> get() = _exitList

    private val _currentExit = MutableStateFlow<Exit?>(null)
    val currentExit: StateFlow<Exit?> get() = _currentExit

    fun currentExit(exit: Exit?) {
        _currentExit.value = exit
    }

    fun loadExits() {
        viewModelScope.launch {
            val response = getAllExitsUseCase()
            if (response.isSuccessful) {
                _exitList.value = response.body() ?: emptyList()
            }
        }
    }

    fun getExitById(id: Int) {
        viewModelScope.launch {
            val response = getExitByIdUseCase(id)
            if (response.isSuccessful) {
                _currentExit.value = response.body()
            }
        }
    }

    fun createExit(exit: Exit, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = createExitUseCase(exit)
            onResult(response.isSuccessful)
        }
    }

    fun updateExit(exit: Exit, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = updateExitUseCase(exit)
            onResult(response.isSuccessful)
        }
    }

    fun deleteExit(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = deleteExitUseCase(id)
            onResult(response.isSuccessful)
        }
    }
}