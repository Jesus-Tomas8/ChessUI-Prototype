package com.example.chessapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.util.Locale
import java.util.UUID

class PlayProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PlayerProfileRepository(application)

    var playerProfile by mutableStateOf(repository.getLocalPlayerProfile())
        private set

    var usernameInput by mutableStateOf(playerProfile?.username.orEmpty())
        private set

    var statusMessage by mutableStateOf(
        if (playerProfile == null) {
            "Create a player profile before searching for a match."
        } else {
            "Profile loaded from the local database."
        }
    )
        private set

    var isSearchingForMatch by mutableStateOf(false)
        private set

    fun updateUsername(value: String) {
        usernameInput = value.take(MAX_USERNAME_LENGTH)
        isSearchingForMatch = false
    }

    fun saveProfile() {
        val username = usernameInput.trim()

        if (username.length < MIN_USERNAME_LENGTH) {
            statusMessage = "Username needs at least $MIN_USERNAME_LENGTH characters."
            return
        }

        val now = System.currentTimeMillis()
        val savedProfile = PlayerProfile(
            id = playerProfile?.id ?: generatePlayerId(),
            username = username,
            createdAt = playerProfile?.createdAt ?: now,
            updatedAt = now
        )

        repository.saveLocalPlayerProfile(savedProfile)
        playerProfile = savedProfile
        usernameInput = savedProfile.username
        isSearchingForMatch = false
        statusMessage = "Saved ${savedProfile.username} for matchmaking."
    }

    fun startMatchmaking() {
        val profile = playerProfile

        if (profile == null) {
            statusMessage = "Save your profile first so matchmaking has a player ID."
            return
        }

        isSearchingForMatch = true
        statusMessage = "Searching with matchmaking ID ${profile.id}."
    }

    fun cancelMatchmaking() {
        isSearchingForMatch = false
        statusMessage = "Matchmaking cancelled."
    }

    private fun generatePlayerId(): String {
        return "PB-${UUID.randomUUID().toString().take(8).uppercase(Locale.US)}"
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 18
    }
}
