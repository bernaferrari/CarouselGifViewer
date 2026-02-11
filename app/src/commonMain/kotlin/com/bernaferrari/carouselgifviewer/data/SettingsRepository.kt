package com.bernaferrari.carouselgifviewer.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    System, Light, Dark, MaterialYou
}

enum class ShaderType {
    // Basic
    Aurora, LavaLamp, Waves, Nebula, Candy, Orbs, Vortex, Wormhole, Silk, Mosaic,
    Plasma, Stardust, Prism,
    
    // Alt Versions
    AuroraAlt, LavaLampAlt, NebulaAlt, WavesAlt, SilkAlt,
    
    // Special
    Dreamy, Starlight, Transit, HexGrid, GoldenRings,
    Ether, Inferno, Cinders, InkBlot, Monochrome,
    Hellfire, Scanline, Radar, Chrome, Zen
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val accentColorIndex: Int = 0,
    val shaderEffectEnabled: Boolean = true,
    val shaderType: ShaderType = ShaderType.Zen,
    val favorites: Set<String> = emptySet(),
    val lastSelectedId: String? = null,
    val playbackSpeed: Float = 1f
)

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR_INDEX = intPreferencesKey("accent_color_index")
        val SHADER_EFFECT_ENABLED = booleanPreferencesKey("shader_effect_enabled")
        val SHADER_TYPE = stringPreferencesKey("shader_type")
        val FAVORITES = stringPreferencesKey("favorites")
        val LAST_SELECTED_ID = stringPreferencesKey("last_selected_id")
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
    }

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME_MODE]?.let { 
                ThemeMode.entries.find { mode -> mode.name == it } 
            } ?: ThemeMode.System,
            accentColorIndex = prefs[Keys.ACCENT_COLOR_INDEX] ?: 0,
            shaderEffectEnabled = prefs[Keys.SHADER_EFFECT_ENABLED] ?: false,
            shaderType = prefs[Keys.SHADER_TYPE]?.let {
                ShaderType.entries.find { type -> type.name == it }
            } ?: ShaderType.Zen,
            favorites = prefs[Keys.FAVORITES]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet(),
            lastSelectedId = prefs[Keys.LAST_SELECTED_ID],
            playbackSpeed = prefs[Keys.PLAYBACK_SPEED] ?: 1f
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

    suspend fun setShaderType(type: ShaderType) {
        dataStore.edit { prefs ->
            prefs[Keys.SHADER_TYPE] = type.name
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

    suspend fun setLastSelectedId(id: String) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_SELECTED_ID] = id
        }
    }
    
    suspend fun setPlaybackSpeed(speed: Float) {
        dataStore.edit { prefs ->
            prefs[Keys.PLAYBACK_SPEED] = speed
        }
    }
}
