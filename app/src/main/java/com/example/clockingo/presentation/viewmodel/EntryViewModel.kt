package com.example.clockingo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockingo.domain.model.Entry
import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EntryViewModel(
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    private val createEntryUseCase: CreateEntryUseCase,
    private val updateEntryUseCase: UpdateEntryUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) : ViewModel() {

    private val _entryList = MutableStateFlow<List<Entry>>(emptyList())
    val entryList: StateFlow<List<Entry>> get() = _entryList

    private val _currentEntry = MutableStateFlow<Entry?>(null)
    val currentEntry: StateFlow<Entry?> get() = _currentEntry

    fun currentEntry(entry: Entry?) {
        _currentEntry.value = entry
    }

    fun loadEntries() {
        viewModelScope.launch {
            val response = getAllEntriesUseCase()
            if (response.isSuccessful) {
                _entryList.value = response.body() ?: emptyList()
            }
        }
    }

    fun getEntryById(id: Int) {
        viewModelScope.launch {
            val response = getEntryByIdUseCase(id)
            if (response.isSuccessful) {
                _currentEntry.value = response.body()
            }
        }
    }

    fun createEntry(entry: Entry, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = createEntryUseCase(entry)
            onResult(response.isSuccessful)
        }
    }

    fun updateEntry(entry: Entry, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = updateEntryUseCase(entry)
            onResult(response.isSuccessful)
        }
    }

    fun deleteEntry(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = deleteEntryUseCase(id)
            onResult(response.isSuccessful)
        }
    }
}