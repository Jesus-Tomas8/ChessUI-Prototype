package com.example.chessapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    var isDarkMode by mutableStateOf(false)
        private set

    var isVolumeOn by mutableStateOf(true)
        private set

    fun updateDarkMode(value: Boolean) {
        isDarkMode = value
    }

    fun updateVolume(value: Boolean) {
        isVolumeOn = value
    }
}