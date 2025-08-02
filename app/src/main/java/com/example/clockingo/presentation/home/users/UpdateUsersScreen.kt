package com.example.clockingo.presentation.home.users

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.clockingo.domain.model.User
import com.example.clockingo.presentation.viewmodel.UserViewModel
import androidx.compose.ui.text.input.KeyboardType
import com.example.clockingo.presentation.viewmodel.RoleViewModel
import androidx.compose.ui.res.stringResource
import com.example.clockingo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUsersScreen(
    userViewModel: UserViewModel,
    roleViewModel: RoleViewModel,
    user: User,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val roleList by roleViewModel.roleList.collectAsState()

    LaunchedEffect(Unit) {
        roleViewModel.loadRoles()
    }

    LaunchedEffect(user.id) {
        userViewModel.getUserById(user.id)
    }

    var name by remember { mutableStateOf(user.name) }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var username by remember { mutableStateOf(user.username) }
    var password by remember { mutableStateOf(user.authToken) }
    var selectedRoleId by remember { mutableIntStateOf(user.roleId) }
    var expanded by remember { mutableStateOf(false) }

    BackHandler {
        onFinish()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.update_user_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.update_user_label_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.update_user_label_phone)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.update_user_label_username)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.update_user_label_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = roleList.find { it.id == selectedRoleId }?.name ?: stringResource(R.string.update_user_label_role_placeholder),
                onValueChange = {},
                label = { Text(stringResource(R.string.update_user_label_role)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                roleList.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.name) },
                        onClick = {
                            selectedRoleId = role.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (name.isNotBlank() && username.isNotBlank() && password.isNotBlank() && selectedRoleId != 0) {
                    val updatedUser = user.copy(
                        name = name,
                        phone = phone.ifBlank { null },
                        username = username,
                        authToken = password,
                        roleId = selectedRoleId
                    )
                    userViewModel.updateUser(updatedUser) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, context.getString(R.string.update_user_success), Toast.LENGTH_SHORT).show()
                            onFinish()
                        } else {
                            Toast.makeText(context, context.getString(R.string.update_user_failed), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.update_user_fill_required), Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text(stringResource(R.string.update_user_button))
        }
    }
}