package com.example.clockingo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockingo.domain.model.Role
import com.example.clockingo.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RoleViewModel(
    private val getAllRolesUseCase: GetAllRolesUseCase,
    private val getRoleByIdUseCase: GetRoleByIdUseCase,
    private val createRoleUseCase: CreateRoleUseCase,
    private val updateRoleUseCase: UpdateRoleUseCase,
    private val deleteRoleUseCase: DeleteRoleUseCase
) : ViewModel() {

    private val _roleList = MutableStateFlow<List<Role>>(emptyList())
    val roleList: StateFlow<List<Role>> get() = _roleList

    private val _currentRole = MutableStateFlow<Role?>(null)
    val currentRole: StateFlow<Role?> get() = _currentRole

    fun currentRole(role: Role?) {
        _currentRole.value = role
    }

    fun loadRoles() {
        viewModelScope.launch {
            val response = getAllRolesUseCase()
            if (response.isSuccessful) {
                _roleList.value = response.body() ?: emptyList()
            }
        }
    }

    fun getRoleById(id: Int) {
        viewModelScope.launch {
            val response = getRoleByIdUseCase(id)
            if (response.isSuccessful) {
                _currentRole.value = response.body()
            }
        }
    }

    fun createRole(role: Role, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = createRoleUseCase(role)
            onResult(response.isSuccessful)
        }
    }

    fun updateRole(role: Role, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = updateRoleUseCase(role)
            onResult(response.isSuccessful)
        }
    }

    fun deleteRole(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = deleteRoleUseCase(id)
            onResult(response.isSuccessful)
        }
    }
}