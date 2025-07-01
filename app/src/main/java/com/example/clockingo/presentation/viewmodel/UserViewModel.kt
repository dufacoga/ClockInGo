package com.example.clockingo.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.usecase.*
import com.example.clockingo.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getUserByUserUseCase: GetUserByUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList: StateFlow<List<User>> get() = _userList

    private val _loggedIn = MutableStateFlow<Boolean?>(null)
    val loggedIn: StateFlow<Boolean?> get() = _loggedIn

    fun resetLoginState() {
        _loggedIn.value = null
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    fun logout() {
        viewModelScope.launch {
            saveLoginState(false)
            resetLoginState()
            _loggedIn.value = false
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
            sessionManager.isLoggedIn.collect {
                saved ->
                _loggedIn.value = saved
            }
        }
    }

    fun getUserByUser(username: String, password: String, onFailure: () -> Unit) {
        viewModelScope.launch {
            val result = getUserByUserUseCase(username, password)

            if (result == true) {
                saveLoginState(true)
                _loggedIn.value = true
            }else{
                saveLoginState(false)
                resetLoginState()
                _loggedIn.value = false
                onFailure()
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
                _currentUser.value = response.body()
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