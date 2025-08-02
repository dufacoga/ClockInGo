package com.example.clockingo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.usecase.*
import com.example.clockingo.data.local.SessionManager
import com.example.clockingo.data.local.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getUserByUserUseCase: GetUserByUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val sessionManager: SessionManager,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList: StateFlow<List<User>> get() = _userList

    private val _loggedIn = MutableStateFlow<Boolean?>(null)
    val loggedIn: StateFlow<Boolean?> get() = _loggedIn

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    private val _userToEdit = MutableStateFlow<User?>(null)

    val isOnline: StateFlow<Boolean> get() = connectivityObserver.isConnected

    fun resetLoginState() {
        _loggedIn.value = null
    }

    fun clearUserToEdit() {
        _userToEdit.value = null
    }

    fun logout() {
        viewModelScope.launch {
            if (connectivityObserver.currentStatus()) {
                saveLoginState(false)
                resetLoginState()
                _loggedIn.value = false
            }
        }
    }
    fun saveLoginState(loggedIn: Boolean) {
        viewModelScope.launch {
            sessionManager.saveLoginState(loggedIn)
            _loggedIn.value = loggedIn
        }
    }

    fun checkIfUserIsLoggedIn() {
        viewModelScope.launch {
            sessionManager.isLoggedIn.collect { saved ->
                _loggedIn.value = saved
                if (saved) {
                    val user = sessionManager.getLoggedInUser()
                    if (user != null) {
                        _currentUser.value = user
                    }
                }
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            val response = getAllUsersUseCase()
            if (response.isSuccessful) {
                _userList.value = response.body() ?: emptyList()
            }
        }
    }

    fun getUserById(id: Int) {
        viewModelScope.launch {
            val response = getUserByIdUseCase(id)
            if (response.isSuccessful) {
                _userToEdit.value = response.body()
            }
        }
    }

    fun getUserByUser(username: String, password: String, onFailure: () -> Unit) {
        viewModelScope.launch {
            if (!connectivityObserver.currentStatus()) {
                onFailure()
                return@launch
            }
            _isLoading.value = true
            val result = getUserByUserUseCase(username, password)
            _isLoading.value = false

            if (result == true) {
                val user = getUserByUserUseCase.loggedInUser
                if (user != null) {
                    sessionManager.saveLoggedInUser(user)
                    _currentUser.value = user
                }
                saveLoginState(true)
                _loggedIn.value = true
            } else {
                saveLoginState(false)
                resetLoginState()
                _loggedIn.value = false
                onFailure()
            }
        }
    }

    fun createUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = createUserUseCase(user)
            onResult(response.isSuccessful)
        }
    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = updateUserUseCase(user)
            onResult(response.isSuccessful)
        }
    }

    fun deleteUser(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = deleteUserUseCase(id)
            onResult(response.isSuccessful)
        }
    }
}