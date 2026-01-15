package com.bernaferrari.carouselgifviewer.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    System, Light, Dark, MaterialYou
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val accentColorIndex: Int = 0,
    val shaderEffectEnabled: Boolean = false,
    val favorites: Set<String> = emptySet()
)

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR_INDEX = intPreferencesKey("accent_color_index")
        val SHADER_EFFECT_ENABLED = booleanPreferencesKey("shader_effect_enabled")
        val FAVORITES = stringPreferencesKey("favorites")
    }

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME_MODE]?.let { 
                ThemeMode.entries.find { mode -> mode.name == it } 
            } ?: ThemeMode.System,
            accentColorIndex = prefs[Keys.ACCENT_COLOR_INDEX] ?: 0,
            shaderEffectEnabled = prefs[Keys.SHADER_EFFECT_ENABLED] ?: false,
            favorites = prefs[Keys.FAVORITES]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
        )
    }

    val favorites: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.FAVORITES]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun setAccentColorIndex(index: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.ACCENT_COLOR_INDEX] = index
        }
    }

    suspend fun setShaderEffectEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.SHADER_EFFECT_ENABLED] = enabled
        }
    }

    suspend fun toggleFavorite(id: String) {
        dataStore.edit { prefs ->
            val currentFavorites = prefs[Keys.FAVORITES]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
            val newFavorites = if (id in currentFavorites) {
                currentFavorites - id
            } else {
                currentFavorites + id
            }
            prefs[Keys.FAVORITES] = newFavorites.joinToString(",")
        }
    }

    suspend fun setFavorites(favorites: Set<String>) {
        dataStore.edit { prefs ->
            prefs[Keys.FAVORITES] = favorites.joinToString(",")
        }
    }
}
