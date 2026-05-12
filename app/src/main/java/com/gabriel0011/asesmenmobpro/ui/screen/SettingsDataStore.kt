package com.gabriel0011.asesmenmobpro.ui.screen

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    private val IS_LIST = booleanPreferencesKey("is_list")
    private val THEME_COLOR = intPreferencesKey("theme_color")

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false
    }

    val isList: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LIST] ?: true
    }

    val themeColor: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[THEME_COLOR] ?: 0
    }
    suspend fun saveThemeSetting(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    suspend fun saveLayout(isListActive: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LIST] = isListActive
        }
    }

    suspend fun saveThemeColor(colorId: Int) {
        context.dataStore.edit { preferences -> preferences[THEME_COLOR] = colorId }
    }
}