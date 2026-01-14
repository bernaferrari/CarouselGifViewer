package com.bernaferrari.carouselgifviewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.carouselgifviewer.data.AppSettings
import com.bernaferrari.carouselgifviewer.data.SettingsRepository
import com.bernaferrari.carouselgifviewer.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun setAccentColorIndex(index: Int) {
        viewModelScope.launch {
            repository.setAccentColorIndex(index)
        }
    }

    fun setShaderEffectEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setShaderEffectEnabled(enabled)
        }
    }
}
