package com.example.clockingo.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    companion object {
        val LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[LOGGED_IN_KEY] ?: false
        }

    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOGGED_IN_KEY] = isLoggedIn
        }
    }

    suspend fun clearLoginState() {
        context.dataStore.edit { preferences ->
            preferences.remove(LOGGED_IN_KEY)
        }
    }
}