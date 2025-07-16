package com.example.clockingo.presentation.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.clockingo.R
import com.example.clockingo.presentation.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    viewModel: UserViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = stringResource(R.string.login_logo_description),
            modifier = Modifier
                .size(128.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(stringResource(R.string.login_title), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(64.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.login_username_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.login_password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Text(stringResource(R.string.login_loading), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (username.isEmpty() && password.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.login_error_empty_both), Toast.LENGTH_SHORT).show()
                } else if (username.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.login_error_empty_username), Toast.LENGTH_SHORT).show()
                } else if (password.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.login_error_empty_password), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.getUserByUser(
                        username,
                        password,
                        onFailure = {
                            Toast.makeText(context, context.getString(R.string.login_error_invalid_credentials), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(R.string.login_button_text), fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}