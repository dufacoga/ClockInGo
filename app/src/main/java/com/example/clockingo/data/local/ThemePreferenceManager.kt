package com.example.clockingo.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.clockingo.ui.theme.ClockInGoThemeOption
import com.example.clockingo.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("theme_preferences")

class ThemePreferenceManager(private val context: Context) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_option")
        private val MODE_KEY = stringPreferencesKey("theme_mode")
    }

    val selectedTheme: Flow<ClockInGoThemeOption> = context.dataStore.data.map { prefs ->
        val name = prefs[THEME_KEY] ?: ClockInGoThemeOption.Green.name
        ClockInGoThemeOption.valueOf(name)
    }

    val selectedMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val name = prefs[MODE_KEY] ?: ThemeMode.System.name
        ThemeMode.valueOf(name)
    }

    suspend fun saveTheme(theme: ClockInGoThemeOption) {
        context.dataStore.edit { it[THEME_KEY] = theme.name }
    }

    suspend fun saveMode(mode: ThemeMode) {
        context.dataStore.edit { it[MODE_KEY] = mode.name }
    }
}