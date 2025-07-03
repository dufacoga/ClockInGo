package com.example.clockingo.presentation.home.users

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.clockingo.presentation.viewmodel.UserViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import com.example.clockingo.presentation.viewmodel.RoleViewModel
import com.example.materialdatatable.MaterialDataTableC
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material3.DataTable
import com.seanproctor.datatable.rememberDataTableState
import kotlinx.coroutines.delay

@Composable
fun FindUsersScreen(
    userViewModel: UserViewModel,
    roleViewModel: RoleViewModel
) {
    val allUsers by userViewModel.userList.collectAsState()
    val roles by roleViewModel.roleList.collectAsState()
    LaunchedEffect(Unit) {
        userViewModel.loadUsers()
        roleViewModel.loadRoles()
    }

    val parentState = rememberLazyListState()
    val childState  = rememberLazyListState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val width: Dp = screenWidth * 0.95f
    val height: Dp = if (isLandscape) {
        screenHeight * 1f
    } else {
        screenHeight * 0.65f
    }

    var searchId by remember { mutableStateOf("") }
    var searchName by remember { mutableStateOf("") }

    LazyColumn(
        state = parentState,
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text("Find Users", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = searchId, onValueChange = {
                        searchId = it;
                    },
                    label = { Text("Find by ID") }, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = searchName, onValueChange = {
                        searchName = it;
                    },
                    label = { Text("Find by Name") }, modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                val headers = listOf("ID", "Name", "Phone", "Username", "Role")

                key(searchId, searchName, configuration.orientation){
                    MaterialDataTableC(
                        headers = headers,
                        dataLoader = { page, pageSize ->
                            while (allUsers.isEmpty()) {
                                delay(100)
                            }

                            val filteredUsers = allUsers.filter {
                                (searchId.isBlank() || it.id.toString().contains(searchId.trim())) &&
                                        (searchName.isBlank() || it.name.contains(searchName.trim(), ignoreCase = true))
                            }

                            val paginatedUsers = filteredUsers.drop((page - 1) * pageSize).take(pageSize)

                            paginatedUsers.map { user ->
                                listOf(
                                    user.id.toString(),
                                    user.name,
                                    user.phone ?: "--- --- ----",
                                    user.username,
                                    roles.find { it.id == user.roleId }?.name ?: "Unknown"
                                )
                            }
                        },
                        onEdit = { rowIndex -> println("Edit user at row: $rowIndex") },
                        onDelete = { rowIndex -> println("Delete user at row: $rowIndex") },
                        editOption = true,
                        deleteOption = true,
                        childState = childState,
                        width = width,
                        height = height
                    )
                }
            }
        }
    }
}