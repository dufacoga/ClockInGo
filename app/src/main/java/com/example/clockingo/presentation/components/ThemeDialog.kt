package com.example.clockingo.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clockingo.ui.theme.ClockInGoThemeOption
import com.example.clockingo.ui.theme.ThemeMode
import com.example.clockingo.ui.theme.toDisplayNameRes

@Composable
fun ShowThemeDialog(
    currentTheme: ClockInGoThemeOption,
    currentMode: ThemeMode,
    onThemeChange: (ClockInGoThemeOption) -> Unit,
    onModeChange: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = { Text("Choose Theme") },
        text = {
            Column {
                Text("Color Theme:")
                ClockInGoThemeOption.values().forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (option == currentTheme),
                            onClick = { onThemeChange(option) }
                        )
                        Text(text = stringResource(id = option.toDisplayNameRes()))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Theme Mode:")
                ThemeMode.values().forEach { mode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (mode == currentMode),
                            onClick = { onModeChange(mode) }
                        )
                        Text(text = stringResource(id = mode.toDisplayNameRes()))
                    }
                }
            }
        }
    )
}