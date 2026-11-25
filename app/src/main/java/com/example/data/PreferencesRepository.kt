package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "civil_calc_prefs")

enum class ThemePreference {
    SYSTEM, LIGHT, DARK
}

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val IS_DEGREE_MODE = booleanPreferencesKey("is_degree_mode")
    }

    val themePreference: Flow<ThemePreference> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME_MODE]) {
            "SYSTEM" -> ThemePreference.SYSTEM
            "DARK" -> ThemePreference.DARK
            else -> ThemePreference.LIGHT
        }
    }

    val isDegreeMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_DEGREE_MODE] ?: true
    }

    suspend fun setThemePreference(pref: ThemePreference) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = pref.name
        }
    }

    suspend fun setDegreeMode(isDegree: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_DEGREE_MODE] = isDegree
        }
    }
}
