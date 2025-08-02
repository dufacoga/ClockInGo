package com.example.clockingo.presentation.home.users

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.clockingo.presentation.viewmodel.UserViewModel
import androidx.compose.ui.unit.Dp
import com.example.clockingo.domain.model.Role
import com.example.clockingo.domain.model.User
import com.example.clockingo.presentation.viewmodel.RoleViewModel
import com.example.materialdatatable.MaterialDataTableC
import com.example.materialdatatable.dataLoaderFromList
import androidx.compose.ui.res.stringResource
import com.example.clockingo.R

@Composable
fun FindUsersScreen(
    userViewModel: UserViewModel,
    roleViewModel: RoleViewModel,
    forUpdate: Boolean,
    forMore: Boolean,
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
    var userDataLoader: suspend (Int, Int) -> List<User>? = { _, _ -> emptyList() }

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
            Text(stringResource(R.string.find_user_title), style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = searchId, onValueChange = {
                        searchId = it;
                    },
                    label = { Text(stringResource(R.string.find_user_search_id)) }, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = searchName, onValueChange = {
                        searchName = it;
                    },
                    label = { Text(stringResource(R.string.find_user_search_name)) }, modifier = Modifier.weight(1f)
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
                        listOf(
                            stringResource(R.string.find_user_header_id),
                            stringResource(R.string.find_user_header_name),
                            stringResource(R.string.find_user_header_phone),
                            stringResource(R.string.find_user_header_username),
                            stringResource(R.string.find_user_header_role)
                        )
                    } else {
                        listOf(
                            stringResource(R.string.find_user_header_id),
                            stringResource(R.string.find_user_header_name),
                            stringResource(R.string.find_user_header_username)
                        )
                    }
                    val filteredUsers = allUsers.filter {
                        (searchId.isBlank() || it.id.toString().contains(searchId.trim())) &&
                                (searchName.isBlank() || it.name.contains(searchName.trim(), ignoreCase = true))
                    }
                    val paginatedUsersCount = filteredUsers.size

                    userDataLoader = dataLoaderFromList(
                        sourceProvider = { filteredUsers }
                    )

                    val placeholderPhone = LocalContext.current.getString(R.string.find_user_placeholder_phone)
                    val placeholderRole = LocalContext.current.getString(R.string.find_user_placeholder_role)

                    val userToRow: (User) -> List<String> = if (isLandscape) {
                        { user ->
                            listOf(
                                user.id.toString(),
                                user.name,
                                user.phone ?: placeholderPhone,
                                user.username,
                                roles.find { it.id == user.roleId }?.name ?: placeholderRole
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

                    MaterialDataTableC(
                        headers = headers,
                        dataLoader = userDataLoader,
                        rowMapper = userToRow,
                        onEdit = {
                            user -> onUserSelected(user)
                            println("Edit user at row: ${user.name}")
                        },
                        onDelete = { user -> println("Delete user at row: ${user.name}") },
                        onMore = { user -> println("More user at row: ${user.name}") },
                        columnSizeAdaptive = true,
                        columnWidth = 150.dp,
                        moreOption = forMore,
                        editOption = forUpdate,
                        deleteOption = false,
                        horizontalDividers = true,
                        verticalDividers = true,
                        paginationRowFixed = true,
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