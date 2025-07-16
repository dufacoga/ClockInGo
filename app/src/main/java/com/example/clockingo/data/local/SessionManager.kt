package com.example.clockingo.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.clockingo.domain.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    companion object {
        val LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val LOGGED_IN_USER_KEY = stringPreferencesKey("logged_in_user")
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

    suspend fun saveLoggedInUser(user: User) {
        val json = Gson().toJson(user)
        context.dataStore.edit { prefs ->
            prefs[LOGGED_IN_USER_KEY] = json
        }
    }

    suspend fun getLoggedInUser(): User? {
        val prefs = context.dataStore.data.first()
        val json = prefs[LOGGED_IN_USER_KEY] ?: return null
        return Gson().fromJson(json, User::class.java)
    }

    suspend fun clearLoginState() {
        context.dataStore.edit { preferences ->
            preferences.remove(LOGGED_IN_KEY)
            preferences.remove(LOGGED_IN_USER_KEY)
        }
    }
}