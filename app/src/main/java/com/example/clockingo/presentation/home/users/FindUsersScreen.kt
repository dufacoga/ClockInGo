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
import com.example.clockingo.domain.model.Role
import com.example.clockingo.domain.model.User
import com.example.clockingo.presentation.viewmodel.RoleViewModel
import com.example.materialdatatable.MaterialDataTableC
import com.example.materialdatatable.dataLoaderFromListWithDelay
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material3.DataTable
import com.seanproctor.datatable.rememberDataTableState
import kotlinx.coroutines.delay

@Composable
fun FindUsersScreen(
    userViewModel: UserViewModel,
    roleViewModel: RoleViewModel,
    forUpdate: Boolean,
    onUserSelected: (User) -> Unit
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
    var isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var userDataLoader: suspend (Int, Int) -> List<List<String>> = { _, _ -> emptyList() }

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
                key(searchId, searchName, configuration.orientation,  userDataLoader){
                    isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    val headers = if (isLandscape) {
                        listOf("ID", "Name", "Phone", "Username", "Role")
                    } else {
                        listOf("ID", "Name", "Username")
                    }
                    val filteredUsers = allUsers.filter {
                        (searchId.isBlank() || it.id.toString().contains(searchId.trim())) &&
                                (searchName.isBlank() || it.name.contains(searchName.trim(), ignoreCase = true))
                    }
                    val paginatedUsersCount = filteredUsers.size
                    val (userRowMapper, rowDataToUserMapper) = getUserRowMappers(isLandscape, roles)
                    userDataLoader = dataLoaderFromListWithDelay(
                        sourceProvider = { filteredUsers },
                        rowMapper = userRowMapper
                    )

                    MaterialDataTableC(
                        headers = headers,
                        dataLoader = userDataLoader,
                        onEdit = { rowIndex, rowData -> onUserSelected(rowDataToUserMapper(rowData)); println("Edit user at row: $rowIndex") },
                        onDelete = { rowIndex, rowData -> println("Delete user at row: $rowIndex") },
                        onMoreVert = { rowIndex, rowData -> println("MoreVert user at row: $rowIndex") },
                        columnSizeAdaptive = true,
                        columnWidth = 150.dp,
                        editOption = forUpdate,
                        deleteOption = false,
                        horizontalDividers = true,
                        verticalDividers = true,
                        childState = childState,
                        width = width,
                        height = height,
                        totalItems = paginatedUsersCount
                    )
                }
            }
        }
    }
}

fun getUserRowMappers(
    isLandscape: Boolean,
    roles: List<Role>
): Pair<(User) -> List<String>, (List<String>) -> User> {

    val userToRow: (User) -> List<String> = if (isLandscape) {
        { user ->
            listOf(
                user.id.toString(),
                user.name,
                user.phone ?: "--- --- ----",
                user.username,
                roles.find { it.id == user.roleId }?.name ?: "Unknown"
            )
        }
    } else {
        { user ->
            listOf(
                user.id.toString(),
                user.name,
                user.username
            )
        }
    }

    val rowToUser: (List<String>) -> User = if (isLandscape) {
        { row ->
            User(
                id = row.getOrNull(0)?.toIntOrNull() ?: 0,
                name = row.getOrNull(1) ?: "",
                phone = row.getOrNull(2).takeIf { it != "--- --- ----" },
                username = row.getOrNull(3) ?: "",
                authToken = "",
                roleId = roles.find { it.name == row.getOrNull(4) }?.id ?: 0
            )
        }
    } else {
        { row ->
            User(
                id = row.getOrNull(0)?.toIntOrNull() ?: 0,
                name = row.getOrNull(1) ?: "",
                phone = null,
                username = row.getOrNull(2) ?: "",
                authToken = "",
                roleId = 0
            )
        }
    }

    return Pair(userToRow, rowToUser)
}