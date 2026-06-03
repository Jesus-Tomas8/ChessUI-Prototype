package com.example.chessapp

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences(
        SETTINGS_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    var isDarkMode by mutableStateOf(preferences.getBoolean(KEY_DARK_MODE, false))
        private set

    var isVolumeOn by mutableStateOf(preferences.getBoolean(KEY_VOLUME_ON, true))
        private set

    var isPieceSlideAnimationOn by mutableStateOf(
        preferences.getBoolean(KEY_PIECE_SLIDE_ANIMATION_ON, false)
    )
        private set

    fun updateDarkMode(value: Boolean) {
        isDarkMode = value
        preferences.edit()
            .putBoolean(KEY_DARK_MODE, value)
            .apply()
    }

    fun updateVolume(value: Boolean) {
        isVolumeOn = value
        preferences.edit()
            .putBoolean(KEY_VOLUME_ON, value)
            .apply()
    }

    fun updatePieceSlideAnimation(value: Boolean) {
        isPieceSlideAnimationOn = value
        preferences.edit()
            .putBoolean(KEY_PIECE_SLIDE_ANIMATION_ON, value)
            .apply()
    }

    companion object {
        private const val SETTINGS_PREFERENCES_NAME = "app_settings_preferences"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_VOLUME_ON = "volume_on"
        private const val KEY_PIECE_SLIDE_ANIMATION_ON = "piece_slide_animation_on"
    }
}
